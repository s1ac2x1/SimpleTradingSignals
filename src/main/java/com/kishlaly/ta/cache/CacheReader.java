package com.kishlaly.ta.cache;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kishlaly.ta.analyze.TaskType;
import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.Timeframe;
import com.kishlaly.ta.model.TimeframeIndicators;
import com.kishlaly.ta.model.indicators.EMA;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.model.indicators.MACD;
import com.kishlaly.ta.model.indicators.Stoch;
import com.kishlaly.ta.utils.Context;
import com.kishlaly.ta.utils.IndicatorUtils;
import com.kishlaly.ta.utils.Quotes;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.StochasticOscillatorDIndicator;
import org.ta4j.core.indicators.StochasticOscillatorKIndicator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
    public static CopyOnWriteArrayList<Future> callsInProgress = new CopyOnWriteArrayList<>();

    public static void checkCache(Timeframe[][] timeframes, TaskType[] tasks) {
        AtomicInteger screenNumber = new AtomicInteger(0);
        Map<Timeframe, Set<String>> missedData = new HashMap<>();
        Arrays.stream(timeframes).forEach(screens -> {
            // проверям только наличие котировок в кэше
            // подразумевается, что загружаются только один Context.aggregationTimeframe
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
                Files.write(Paths.get(getFolder() + "/missed.txt"), quotes.stream().collect(Collectors.joining(System.lineSeparator())).getBytes());
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
            try {
                stocksRaw = Files.readAllLines(new File(Context.source).toPath(),
                        Charset.defaultCharset());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        return new HashSet<>(stocksRaw);
    }

    public static Set<String> getMissedSymbols() {
        List<String> stocksRaw = new ArrayList<>();
        try {
            stocksRaw = Files.readAllLines(new File(getFolder() + "/missed.txt").toPath(),
                    Charset.defaultCharset());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return new HashSet<>(stocksRaw);
    }

    public static List<String> removeCachedIndicatorSymbols(Set<String> src, Indicator indicator) {
        return src.stream().filter(symbol -> {
            File file = new File(getFolder() + "/" + symbol + "_" + indicator.name().toLowerCase() + ".txt");
            return !file.exists();
        }).collect(Collectors.toList());
    }

    public static List<String> removeCachedQuotesSymbols(Set<String> src) {
        return src.stream().filter(symbol -> {
            File file = new File(getFolder() + "/" + symbol + "_quotes.txt");
            return !file.exists();
        }).collect(Collectors.toList());
    }

    public static List<Quote> loadQuotesFromDiskCache(String symbol) {
        List<Quote> cachedQuotes = QuotesInMemoryCache.get(symbol, Context.timeframe);
        if (!cachedQuotes.isEmpty()) {
            return cachedQuotes;
        } else {
            try {
                List<Quote> quotes = gson.fromJson(
                        new String(
                                Files.readAllBytes(Paths.get(getFolder() + "/" + symbol + "_quotes.txt"))),
                        new TypeToken<ArrayList<Quote>>() {
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
                quotes = quotes.stream().filter(Quote::valuesPesent).collect(Collectors.toList());
                QuotesInMemoryCache.put(symbol, Context.timeframe, quotes);
                return quotes;
            } catch (IOException e) {
                return Collections.emptyList();
            }
        }
    }

    public static List calculateIndicatorFromCachedQuotes(String symbol, Indicator indicator) {
        switch (indicator) {
            case MACD:
                return IndicatorUtils.buildMACDHistogram(symbol);
            case EMA13:
                return IndicatorUtils.buildEMA(symbol, 13);
            case EMA26:
                return IndicatorUtils.buildEMA(symbol, 26);
            case STOCH:
                return IndicatorUtils.buildStochastic(symbol);
            case KELTNER:
                return IndicatorUtils.buildKeltnerChannels(symbol);
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
                    Files.readAllBytes(Paths.get(getFolder() + "/" + symbol + "_" + indicator.name().toLowerCase() + ".txt")));
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
        screen.quotes = loadQuotesFromDiskCache(symbol);
        Arrays.stream(timeframeIndicators.indicators).forEach(indicator -> {
            List data = calculateIndicatorFromCachedQuotes(symbol, indicator);
            screen.indicators.put(indicator, data);
        });
        return screen;
    }

    public static String getFolder() {
        return Context.outputFolder + "/cache/" + Context.aggregationTimeframe.name().toLowerCase();
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
