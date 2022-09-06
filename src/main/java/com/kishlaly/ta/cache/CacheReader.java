package com.kishlaly.ta.cache;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kishlaly.ta.analyze.TaskType;
import com.kishlaly.ta.model.*;
import com.kishlaly.ta.model.indicators.EMA;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.model.indicators.MACD;
import com.kishlaly.ta.model.indicators.Stoch;
import com.kishlaly.ta.utils.Context;
import com.kishlaly.ta.utils.IndicatorUtils;
import com.kishlaly.ta.utils.Quotes;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.kishlaly.ta.utils.Context.fileSeparator;
import static com.kishlaly.ta.utils.DatesJava.shortDateToZoned;

/**
 * @author Vladimir Kishlaly
 * @since 18.11.2021
 */
public class CacheReader {

    public static Gson gson = new Gson();
    public static ScheduledExecutorService queueExecutor = Executors.newScheduledThreadPool(1);
    public static ExecutorService apiExecutor = Executors.newCachedThreadPool();
    public static int requestPeriod;
    public static ConcurrentLinkedDeque<LoadRequest> requests = new ConcurrentLinkedDeque<>();
    public static List<Future> callsInProgress = new CopyOnWriteArrayList<>();

    public static void checkCache(Timeframe[][] timeframes, TaskType[] tasks) {
        AtomicInteger screenNumber = new AtomicInteger(0);
        Map<Timeframe, Set<String>> missedData = new HashMap<>();
        Arrays.stream(timeframes).forEach(screens -> {
            // only check the availability of quotes in the cache
            // it is assumed that only one Context.aggregationTimeframe is loaded
            screenNumber.getAndIncrement();
            Context.timeframe = Context.aggregationTimeframe;
            List<String> missingQuotes = removeCachedQuotesSymbols(Context.symbols);
            Set<String> missingQuotesCollectedByScreen1 = missedData.get(screens[0]);
            if (missingQuotesCollectedByScreen1 == null) {
                missingQuotesCollectedByScreen1 = new HashSet<>();
            }
            missingQuotesCollectedByScreen1.addAll(missingQuotes);
            missedData.put(Context.timeframe, missingQuotesCollectedByScreen1);
        });
        missedData.forEach((tf, quotes) -> {
            Context.timeframe = tf;
            try {
                Files.write(Paths.get(getFolder() + fileSeparator + "missed.txt"), quotes.stream().collect(Collectors.joining(System.lineSeparator())).getBytes());
                System.out.println("Logged " + quotes.size() + " missed " + tf.name() + " quotes");
            } catch (IOException e) {
                System.out.println("Couldn't log missed quotes");
            }
        });
    }

    public static Set<String> getSymbols() {
        List<String> stocksRaw = new ArrayList<>();
        if (!Context.testOnly.isEmpty()) {
            stocksRaw.addAll(Context.testOnly);
        } else {
            Arrays.stream(Context.source).forEach(source -> {
                try {
                    List<String> lines = Files.readAllLines(new File(Context.outputFolder + "/" + source.getFilename()).toPath(),
                            Charset.defaultCharset());
                    if (source.isRandom()) {
                        Collections.shuffle(lines);
                        lines = lines.stream().limit(30).collect(Collectors.toList());
                    }
                    stocksRaw.addAll(lines);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        return new HashSet<>(stocksRaw);
    }

    public static Set<String> getMissedSymbols() {
        List<String> stocksRaw = new ArrayList<>();
        try {
            stocksRaw = Files.readAllLines(new File(getFolder() + fileSeparator + "missed.txt").toPath(),
                    Charset.defaultCharset());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return new HashSet<>(stocksRaw);
    }

    public static List<String> removeCachedIndicatorSymbols(Set<String> src, Indicator indicator) {
        return src.stream().filter(symbol -> {
            File file = new File(getFolder() + fileSeparator + symbol + "_" + indicator.name().toLowerCase() + ".txt");
            return !file.exists();
        }).collect(Collectors.toList());
    }

    public static List<String> removeCachedQuotesSymbols(Set<String> src) {
        return src.stream().filter(symbol -> {
            File file = new File(getFolder() + fileSeparator + symbol + "_quotes.txt");
            return !file.exists();
        }).collect(Collectors.toList());
    }

    public static List<QuoteJava> loadQuotesFromDiskCache(String symbol) {
        List<QuoteJava> cachedQuotes = QuotesInMemoryCache.get(symbol, Context.timeframe);
        if (!cachedQuotes.isEmpty()) {
            return cachedQuotes;
        } else {
            try {
                List<QuoteJava> quotes = gson.fromJson(
                        new String(
                                Files.readAllBytes(Paths.get(getFolder() + fileSeparator + symbol + "_quotes.txt"))),
                        new TypeToken<ArrayList<QuoteJava>>() {
                        }.getType());
                switch (Context.aggregationTimeframe) {
                    case DAY:
                        if (Context.timeframe == Timeframe.WEEK) {
                            quotes = Quotes.dayToWeek(quotes);
                        }
                        if (Context.timeframe == Timeframe.HOUR) {
                            throw new RuntimeException("Requested HOUR quotes, but aggregationTimeframe = DAY");
                        }
                        break;
                    case HOUR:
                        if (Context.timeframe == Timeframe.WEEK) {
                            quotes = Quotes.hourToDay(quotes);
                            quotes = Quotes.dayToWeek(quotes);
                        }
                        if (Context.timeframe == Timeframe.DAY) {
                            quotes = Quotes.hourToDay(quotes);
                        }
                        break;
                    default:
                }
                quotes = quotes.stream().filter(QuoteJava::valuesPesent).collect(Collectors.toList());
                Collections.sort(quotes, Comparator.comparing(QuoteJava::getTimestamp));
                if (Context.trimToDate != null) {
                    ZonedDateTime filterAfter = shortDateToZoned(Context.trimToDate);
                    quotes = quotes.stream().filter(quote -> quote.getTimestamp() <= filterAfter.toEpochSecond()).collect(Collectors.toList());
                }
                QuotesInMemoryCache.put(symbol, Context.timeframe, quotes);
                return quotes;
            } catch (IOException e) {
                return Collections.emptyList();
            }
        }
    }

    public static List calculateIndicatorFromCachedQuotes(String symbol, Indicator indicator) {
        List<QuoteJava> quotes = loadQuotesFromDiskCache(symbol);
        switch (indicator) {
            case MACD:
                return IndicatorUtils.buildMACDHistogram(symbol, quotes);
            case EMA13:
                return IndicatorUtils.buildEMA(symbol, quotes, 13);
            case EMA26:
                return IndicatorUtils.buildEMA(symbol, quotes, 26);
            case STOCH:
                return IndicatorUtils.buildStochastic(symbol, quotes);
            case KELTNER:
                return IndicatorUtils.buildKeltnerChannels(symbol, quotes);
            case BOLLINGER:
                return IndicatorUtils.buildBollingerBands(symbol, quotes);
            case EFI:
                return IndicatorUtils.buildEFI(symbol, quotes);
            default:
                return Collections.emptyList();
        }
    }

    public static List loadIndicatorFromDiskCache(String symbol, Indicator indicator) {
        try {
            Type type;
            switch (indicator) {
                case MACD:
                    type = new TypeToken<ArrayList<MACD>>() {
                    }.getType();
                    break;
                case EMA13:
                case EMA26:
                    type = new TypeToken<ArrayList<EMA>>() {
                    }.getType();
                    break;
                case STOCH:
                    type = new TypeToken<ArrayList<Stoch>>() {
                    }.getType();
                    break;
                default:
                    type = null;
            }
            String json = new String(
                    Files.readAllBytes(Paths.get(getFolder() + fileSeparator + symbol + "_" + indicator.name().toLowerCase() + ".txt")));
            List list = gson.fromJson(
                    json,
                    type);
            switch (indicator) {
                case MACD:
                    Collections.sort(list, Comparator.comparing(MACD::getTimestamp));
                    break;
                case EMA13:
                case EMA26:
                    Collections.sort(list, Comparator.comparing(EMA::getTimestamp));
                    break;
                case STOCH:
                    Collections.sort(list, Comparator.comparing(Stoch::getTimestamp));
                    break;
            }
            return list;
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    public static SymbolData getSymbolData(TimeframeIndicators timeframeIndicators, String symbol) {
        Context.timeframe = timeframeIndicators.timeframe;
        SymbolData screen = new SymbolData();
        screen.symbol = symbol;
        screen.timeframe = timeframeIndicators.timeframe;
        List<QuoteJava> quotes = loadQuotesFromDiskCache(symbol);
        screen.quotes = quotes;
        Arrays.stream(timeframeIndicators.indicators).forEach(indicator -> {
            List<? extends AbstractModelJava> data = calculateIndicatorFromCachedQuotes(symbol, indicator);
            screen.indicators.put(indicator, data);
        });
        return screen;
    }

    public static String getFolder() {
        return Context.outputFolder + fileSeparator + "cache" + fileSeparator + Context.aggregationTimeframe.name().toLowerCase();
    }

    public static void clearCacheFolder(String name) {
        try {
            Files.walk(Paths.get(name))
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
