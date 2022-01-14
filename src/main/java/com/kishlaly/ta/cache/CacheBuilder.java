package com.kishlaly.ta.cache;

import com.google.common.collect.Lists;
import com.kishlaly.ta.analyze.TaskType;
import com.kishlaly.ta.loaders.Alphavantage;
import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.Timeframe;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.utils.Context;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.kishlaly.ta.cache.CacheReader.*;
import static com.kishlaly.ta.utils.Context.*;

public class CacheBuilder {

    /**
     * cache/
     * -----{timeframe}/
     * -----------------{symbol}_quotes.txt
     * -----------------{symbol}_ema13.txt
     * -----------------{symbol}_ema26.txt
     * -----------------{symbol}_macd.txt
     * -----------------{symbol}_stoch.txt
     */
    public static void buildCache(Timeframe[][] timeframes, TaskType[] tasks, boolean reloadMissed) {
        String folder = Context.outputFolder + "/cache";
        File directory = new File(folder);
        if (!directory.exists()) {
            directory.mkdir();
        }
        AtomicReference<Set<String>> symbols = new AtomicReference<>(getSymbols());
        Arrays.stream(timeframes).forEach(screens -> {
            Arrays.stream(tasks).forEach(task -> {
                // загружаются только один таймфрейм Context.aggregationTimeframe
                Context.timeframe = aggregationTimeframe;
                if (reloadMissed) {
                    symbols.set(getMissedSymbols());
                }
                cacheQuotes(symbols.get());
            });
        });
        double p = limitPerMinute / parallelRequests;
        requestPeriod = (int) (p * 1000) + 1000; // +1 секунда для запаса
        queueExecutor.scheduleAtFixedRate(() -> processQueue(), requestPeriod, requestPeriod, TimeUnit.MILLISECONDS);
        if (reloadMissed) {
            Arrays.stream(timeframes).forEach(screens -> {
                Arrays.stream(screens).forEach(screen -> {
                    Context.timeframe = screen;
                    File file = new File(getFolder() + "/missed.txt");
                    if (file.exists()) {
                        file.delete();
                    }
                });
            });
        }
    }

    public static void processQueue() {
        System.out.println("");
        int seconds = requests.size() * requestPeriod / 1000;
        int hours = seconds / 3600;
        int remainderSeconds = seconds - hours * 3600;
        int mins = remainderSeconds / 60;
        remainderSeconds = remainderSeconds - mins * 60;
        int secs = remainderSeconds;
        System.out.println(hours + ":" + mins + ":" + secs + " left...");
        if (requests.size() == 0) {
            queueExecutor.shutdownNow();
        }
        // на запускать новые запросы к API, если еще не вся прошлая партия завершилась
        for (int i = 0; i < callsInProgress.size(); i++) {
            if (!callsInProgress.get(i).isDone()) {
                System.out.println("Previous batch is still in progress, skipping this round");
                return;
            }
        }
        LoadRequest request = requests.poll();
        if (request != null) {
            List<String> symbols = request.getSymbols();
            Timeframe timeframe = request.getTimeframe();
            Context.timeframe = timeframe;
            if (request.getType() == CacheType.QUOTE) {
                System.out.println("Loading " + timeframe.name() + " quotes...");
                symbols.forEach(symbol -> {
                    Future<?> future = apiExecutor.submit(() -> {
                        List<Quote> quotes = Alphavantage.loadQuotes(symbol, timeframe);
                        if (!quotes.isEmpty()) {
                            saveQuote(symbol, quotes);
                        }
                    });
                    callsInProgress.add(future);
                });
            }
            if (request.getType() == CacheType.INDICATOR) {
                Indicator indicator = request.getIndicator();
                System.out.println("Loading " + timeframe.name() + " " + indicator.name() + "...");
                symbols.forEach(symbol -> {
                    Future<?> future = apiExecutor.submit(() -> {
                        List indicatorValue = loadIndicatorFromProvider(symbol, indicator);
                        if (!indicatorValue.isEmpty()) {
                            saveIndicator(symbol, indicator, indicatorValue);
                        }
                    });
                    callsInProgress.add(future);
                });
            }
        }
    }

    private static void saveIndicator(String symbol, Indicator indicator, List values) {
        try {
            String folder = Context.outputFolder + "/cache/" + Context.timeframe.name().toLowerCase();
            File directory = new File(folder);
            if (!directory.exists()) {
                directory.mkdir();
            }
            String json = gson.toJson(values);
            Files.write(Paths.get(folder + "/" + symbol + "_" + indicator.name().toLowerCase() + ".txt"), json.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List loadIndicatorFromProvider(String symbol, Indicator indicator) {
        switch (indicator) {
            case MACD:
                return Alphavantage.loadMACD(symbol);
            case EMA26:
                return Alphavantage.loadEMA(symbol, 26);
            case EMA13:
                return Alphavantage.loadEMA(symbol, 13);
            case STOCH:
                return Alphavantage.loadStoch(symbol);
            default:
                return Collections.emptyList();
        }
    }

    private static void cacheQuotes(Set<String> symbols) {
        List<String> symbolsToCache = removeCachedQuotesSymbols(symbols);
        if (symbolsToCache.isEmpty()) {
            System.out.println(Context.timeframe.name() + " quotes already cached");
            return;
        }
        Lists.partition(symbolsToCache, (int) parallelRequests)
                .forEach(chunk -> {
                    Optional<LoadRequest> existingRequest = requests.stream().filter(request -> request.getType() == CacheType.QUOTE
                            && request.getTimeframe() == Context.timeframe
                            && request.getSymbols().containsAll(chunk)).findFirst();
                    if (!existingRequest.isPresent()) {
                        requests.offer(new LoadRequest(CacheType.QUOTE, Context.timeframe, chunk));
                    } else {
                        System.out.println("Already in the queue: " + chunk.size() + " " + Context.timeframe.name() + " QUOTE");
                    }
                });
    }

    private static void cacheIndicators(Set<String> symbols, Indicator[] indicators) {
        Arrays.stream(indicators).forEach(indicator -> {
            List<String> symbolsToCache = removeCachedIndicatorSymbols(symbols, indicator);
            if (symbolsToCache.isEmpty()) {
                System.out.println(Context.timeframe + " " + indicator.name() + " already cached");
                return;
            }
            Lists.partition(symbolsToCache, (int) parallelRequests)
                    .forEach(chunk -> {
                        Optional<LoadRequest> existingRequest = requests.stream().filter(request -> request.getType() == CacheType.INDICATOR
                                && request.getTimeframe() == Context.timeframe
                                && request.getIndicator() == indicator
                                && request.getSymbols().containsAll(chunk)).findFirst();
                        if (!existingRequest.isPresent()) {
                            LoadRequest loadRequest = new LoadRequest(CacheType.INDICATOR, Context.timeframe, chunk);
                            loadRequest.setIndicator(indicator);
                            requests.offer(loadRequest);
                        } else {
                            System.out.println("Already in the queue: " + chunk.size() + " " + Context.timeframe.name() + " " + indicator.name());
                        }
                    });
        });
    }

    private static void saveQuote(String symbol, List<Quote> quotes) {
        try {
            String folder = Context.outputFolder + "/cache/" + Context.timeframe.name().toLowerCase();
            File directory = new File(folder);
            if (!directory.exists()) {
                directory.mkdir();
            }
            String json = gson.toJson(quotes);
            Files.write(Paths.get(folder + "/" + symbol + "_quotes.txt"), json.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
