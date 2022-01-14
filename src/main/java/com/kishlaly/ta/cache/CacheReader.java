package com.kishlaly.ta.cache;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kishlaly.ta.analyze.TaskType;
import com.kishlaly.ta.model.*;
import com.kishlaly.ta.model.indicators.EMA;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.model.indicators.MACD;
import com.kishlaly.ta.model.indicators.Stoch;
import com.kishlaly.ta.utils.Bars;
import com.kishlaly.ta.utils.Context;
import com.kishlaly.ta.utils.IndicatorUtils;
import com.kishlaly.ta.utils.Quotes;
import org.ta4j.core.BarSeries;
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
        Set<String> allSymbols = getSymbols();
        AtomicInteger screenNumber = new AtomicInteger(0);
        Map<Timeframe, Set<String>> missedData = new HashMap<>();
        Arrays.stream(timeframes).forEach(screens -> {
            // проверям только наличие котировок в кэше
            // подразумевается, что загружаются только один Context.aggregationTimeframe
            screenNumber.getAndIncrement();
            Context.timeframe = Context.aggregationTimeframe;
            List<String> missingQuotes = removeCachedQuotesSymbols(allSymbols);
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
        if (Context.singleSymbol != null) {
            stocksRaw.add(Context.singleSymbol);
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

    public static List<Quote> loadQuotesFromCache(String symbol) {
        try {
            List<Quote> quotes = gson.fromJson(
                    new String(
                            Files.readAllBytes(Paths.get(getFolder() + "/" + symbol + "_quotes.txt"))),
                    new TypeToken<ArrayList<Quote>>() {
                    }.getType());
            if (Context.aggregationTimeframe == Timeframe.DAY && Context.timeframe == Timeframe.WEEK) {
                quotes = Quotes.dailyToWeekly(quotes);
            }
            // TODO подумать про базовую агрегацию на основе часового фрейма
            Collections.sort(quotes, Comparator.comparing(Quote::getTimestamp));
            return quotes;
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    public static List calculateIndicatorFromCachedQuotes(String symbol, Indicator indicator) {
        List<Quote> quotes = loadQuotesFromCache(symbol);
        if (quotes.isEmpty()) {
            return Collections.emptyList();
        }
        Collections.sort(quotes, Comparator.comparing(Quote::getTimestamp));
        BarSeries bars = Bars.build(quotes);
        switch (indicator) {
            case MACD:
                List<HistogramQuote> histogramQuotes = IndicatorUtils.buildMACDHistogram(bars, quotes);
                List<MACD> macd = new ArrayList<>();
                histogramQuotes.forEach(histogramQuote -> {
                    macd.add(new MACD(histogramQuote.getQuote().getTimestamp(), 0d, 0d, histogramQuote.getHistogramValue()));
                });
                return macd;
            case EMA13:
                EMAIndicator ema13 = IndicatorUtils.buildEMA(bars, 13);
                List<EMA> result = new ArrayList<>();
                for (int i = 0; i < ema13.getBarSeries().getBarCount(); i++) {
                    result.add(new EMA(quotes.get(i).getTimestamp(), ema13.getValue(i).doubleValue()));
                }
                return result;
            case EMA26:
                EMAIndicator ema26 = IndicatorUtils.buildEMA(bars, 26);
                List<EMA> res = new ArrayList<>();
                for (int i = 0; i < ema26.getBarSeries().getBarCount(); i++) {
                    res.add(new EMA(quotes.get(i).getTimestamp(), ema26.getValue(i).doubleValue()));
                }
                return res;
            case STOCH:
                StochasticOscillatorKIndicator stochK = new StochasticOscillatorKIndicator(bars, 14);
                StochasticOscillatorDIndicator stochD = new StochasticOscillatorDIndicator(stochK);
                List<Stoch> stoch = new ArrayList<>();
                for (int i = 0; i < quotes.size(); i++) {
                    try {
                        stoch.add(new Stoch(quotes.get(i).getTimestamp(), stochD.getValue(i).doubleValue(), stochK.getValue(i).doubleValue()));
                    } catch (NumberFormatException e) {
                        System.out.println(e.getMessage());
                    }
                }
                return stoch;
            case KELTNER:
                return IndicatorUtils.buildKeltnerChannels(quotes);
            default:
                return Collections.emptyList();
        }
    }

    public static List loadIndicatorFromCache(String symbol, Indicator indicator) {
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
        screen.quotes = loadQuotesFromCache(symbol);
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
