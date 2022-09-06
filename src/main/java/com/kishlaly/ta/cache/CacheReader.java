package com.kishlaly.ta.cache;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kishlaly.ta.analyze.TaskType;
import com.kishlaly.ta.model.*;
import com.kishlaly.ta.model.indicators.EMAJava;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.model.indicators.MACDJava;
import com.kishlaly.ta.model.indicators.StochJava;
import com.kishlaly.ta.utils.ContextJava;
import com.kishlaly.ta.utils.IndicatorUtilsJava;
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

import static com.kishlaly.ta.utils.ContextJava.fileSeparator;
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

    public static void checkCache(TimeframeJava[][] timeframes, TaskType[] tasks) {
        AtomicInteger screenNumber = new AtomicInteger(0);
        Map<TimeframeJava, Set<String>> missedData = new HashMap<>();
        Arrays.stream(timeframes).forEach(screens -> {
            // only check the availability of quotes in the cache
            // it is assumed that only one Context.aggregationTimeframe is loaded
            screenNumber.getAndIncrement();
            ContextJava.timeframe = ContextJava.aggregationTimeframe;
            List<String> missingQuotes = removeCachedQuotesSymbols(ContextJava.symbols);
            Set<String> missingQuotesCollectedByScreen1 = missedData.get(screens[0]);
            if (missingQuotesCollectedByScreen1 == null) {
                missingQuotesCollectedByScreen1 = new HashSet<>();
            }
            missingQuotesCollectedByScreen1.addAll(missingQuotes);
            missedData.put(ContextJava.timeframe, missingQuotesCollectedByScreen1);
        });
        missedData.forEach((tf, quotes) -> {
            ContextJava.timeframe = tf;
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
        if (!ContextJava.testOnly.isEmpty()) {
            stocksRaw.addAll(ContextJava.testOnly);
        } else {
            Arrays.stream(ContextJava.source).forEach(source -> {
                try {
                    List<String> lines = Files.readAllLines(new File(ContextJava.outputFolder + "/" + source.getFilename()).toPath(),
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

    public static List<String> removeCachedIndicatorSymbols(Set<String> src, IndicatorJava indicator) {
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
        List<QuoteJava> cachedQuotes = QuotesInMemoryCache.get(symbol, ContextJava.timeframe);
        if (!cachedQuotes.isEmpty()) {
            return cachedQuotes;
        } else {
            try {
                List<QuoteJava> quotes = gson.fromJson(
                        new String(
                                Files.readAllBytes(Paths.get(getFolder() + fileSeparator + symbol + "_quotes.txt"))),
                        new TypeToken<ArrayList<QuoteJava>>() {
                        }.getType());
                switch (ContextJava.aggregationTimeframe) {
                    case DAY:
                        if (ContextJava.timeframe == TimeframeJava.WEEK) {
                            quotes = Quotes.dayToWeek(quotes);
                        }
                        if (ContextJava.timeframe == TimeframeJava.HOUR) {
                            throw new RuntimeException("Requested HOUR quotes, but aggregationTimeframe = DAY");
                        }
                        break;
                    case HOUR:
                        if (ContextJava.timeframe == TimeframeJava.WEEK) {
                            quotes = Quotes.hourToDay(quotes);
                            quotes = Quotes.dayToWeek(quotes);
                        }
                        if (ContextJava.timeframe == TimeframeJava.DAY) {
                            quotes = Quotes.hourToDay(quotes);
                        }
                        break;
                    default:
                }
                quotes = quotes.stream().filter(QuoteJava::valuesPesent).collect(Collectors.toList());
                Collections.sort(quotes, Comparator.comparing(QuoteJava::getTimestamp));
                if (ContextJava.trimToDate != null) {
                    ZonedDateTime filterAfter = shortDateToZoned(ContextJava.trimToDate);
                    quotes = quotes.stream().filter(quote -> quote.getTimestamp() <= filterAfter.toEpochSecond()).collect(Collectors.toList());
                }
                QuotesInMemoryCache.put(symbol, ContextJava.timeframe, quotes);
                return quotes;
            } catch (IOException e) {
                return Collections.emptyList();
            }
        }
    }

    public static List calculateIndicatorFromCachedQuotes(String symbol, IndicatorJava indicator) {
        List<QuoteJava> quotes = loadQuotesFromDiskCache(symbol);
        switch (indicator) {
            case MACD:
                return IndicatorUtilsJava.buildMACDHistogram(symbol, quotes);
            case EMA13:
                return IndicatorUtilsJava.buildEMA(symbol, quotes, 13);
            case EMA26:
                return IndicatorUtilsJava.buildEMA(symbol, quotes, 26);
            case STOCH:
                return IndicatorUtilsJava.buildStochastic(symbol, quotes);
            case KELTNER:
                return IndicatorUtilsJava.buildKeltnerChannels(symbol, quotes);
            case BOLLINGER:
                return IndicatorUtilsJava.buildBollingerBands(symbol, quotes);
            case EFI:
                return IndicatorUtilsJava.buildEFI(symbol, quotes);
            default:
                return Collections.emptyList();
        }
    }

    public static List loadIndicatorFromDiskCache(String symbol, IndicatorJava indicator) {
        try {
            Type type;
            switch (indicator) {
                case MACD:
                    type = new TypeToken<ArrayList<MACDJava>>() {
                    }.getType();
                    break;
                case EMA13:
                case EMA26:
                    type = new TypeToken<ArrayList<EMAJava>>() {
                    }.getType();
                    break;
                case STOCH:
                    type = new TypeToken<ArrayList<StochJava>>() {
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
                    Collections.sort(list, Comparator.comparing(MACDJava::getTimestamp));
                    break;
                case EMA13:
                case EMA26:
                    Collections.sort(list, Comparator.comparing(EMAJava::getTimestamp));
                    break;
                case STOCH:
                    Collections.sort(list, Comparator.comparing(StochJava::getTimestamp));
                    break;
            }
            return list;
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    public static SymbolDataJava getSymbolData(TimeframeIndicatorsJava timeframeIndicators, String symbol) {
        ContextJava.timeframe = timeframeIndicators.timeframe;
        SymbolDataJava screen = new SymbolDataJava();
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
        return ContextJava.outputFolder + fileSeparator + "cache" + fileSeparator + ContextJava.aggregationTimeframe.name().toLowerCase();
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
