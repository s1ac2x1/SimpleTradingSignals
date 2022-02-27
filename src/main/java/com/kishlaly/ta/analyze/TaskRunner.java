package com.kishlaly.ta.analyze;

import com.kishlaly.ta.analyze.testing.sl.StopLossStrategy;
import com.kishlaly.ta.analyze.testing.tp.TakeProfitStrategy;
import com.kishlaly.ta.cache.CacheReader;
import com.kishlaly.ta.cache.IndicatorsInMemoryCache;
import com.kishlaly.ta.cache.QuotesInMemoryCache;
import com.kishlaly.ta.model.*;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.utils.Context;
import com.kishlaly.ta.utils.Log;
import com.kishlaly.ta.utils.Numbers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kishlaly.ta.Main.getSLStrategies;
import static com.kishlaly.ta.Main.getTPStrategies;
import static com.kishlaly.ta.analyze.testing.TaskTester.test;
import static com.kishlaly.ta.cache.CacheReader.*;
import static com.kishlaly.ta.model.indicators.Indicator.MACD;

/**
 * @author Vladimir Kishlaly
 * @since 18.11.2021
 */
public class TaskRunner {

    public static List<Signal> signals = new ArrayList<>();

    public static void run(Timeframe[][] timeframes, TaskType[] tasks, boolean findOptimal) {
        Arrays.stream(timeframes).forEach(screens -> Arrays.stream(tasks).forEach(task -> {
            task.updateTimeframeForScreen(1, screens[0]);
            task.updateTimeframeForScreen(2, screens[1]);
            Context.logTimeframe1 = screens[0];
            Context.logTimeframe2 = screens[1];
            twoTimeframeFunction(task);
            System.out.println("\n");
            saveLog(task);
        }));
        if (findOptimal) {
            QuotesInMemoryCache.clear();
            IndicatorsInMemoryCache.clear();
            System.gc();
            findOptimalSLTP();
        }
    }

    private static void findOptimalSLTP() {
        List<String> suggestions = new ArrayList<>();
        AtomicInteger symbolNumber = new AtomicInteger(1);
        int totalSymbols = signals.size();
        List<StopLossStrategy> slStrategies = getSLStrategies();
        List<TakeProfitStrategy> tpStrategies = getTPStrategies();
        int totalSLTPStrategies = slStrategies.size() * tpStrategies.size();
        signals.forEach(signal -> {
            AtomicInteger testingStrategySet = new AtomicInteger(1);
            List<HistoricalTesting> result = new ArrayList<>();
            slStrategies.forEach(stopLossStrategy -> {
                tpStrategies.forEach(takeProfitStrategy -> {
                    Context.stopLossStrategy = stopLossStrategy;
                    Context.takeProfitStrategy = takeProfitStrategy;
                    Timeframe[][] timeframes = {
                            {signal.timeframe1, signal.timeframe2},
                    };
                    System.out.println("Testing symbol " + symbolNumber.get() + "/" + totalSymbols + " with TP/SL " + testingStrategySet.get() + "/" + totalSLTPStrategies);
                    Context.symbols = new HashSet<String>() {{
                        add(signal.symbol);
                    }};
                    result.addAll(test(timeframes, new TaskType[]{signal.task}));
                    testingStrategySet.getAndIncrement();
                });
            });
            SymbolData screen2 = getSymbolData(signal.task.getTimeframeIndicators(2), signal.symbol);
            Collections.sort(result, Comparator.comparing(HistoricalTesting::getBalance));
            HistoricalTesting best = result.get(result.size() - 1);
            double stopLoss = best.getStopLossStrategy().calculate(screen2, screen2.quotes.size() - 1);
            double takeProfit = best.getTakeProfitStrategy().calcualte(screen2, screen2.quotes.size() - 1);
            suggestions.add(signal.symbol + " SL: " + Numbers.round(stopLoss) + "; TP: " + Numbers.round(takeProfit) + " [" + best.getStopLossStrategy() + " ... " + best.getTakeProfitStrategy() + "]");
            symbolNumber.getAndIncrement();
        });
        if (!suggestions.isEmpty()) {
            try {
                String prefix = "[" + Context.logTimeframe1.name() + "][" + Context.logTimeframe2.name() + "]";
                String fileName = Context.outputFolder + "/signal/optimal.txt";
                Files.write(Paths.get(fileName), suggestions.stream().collect(Collectors.joining(System.lineSeparator())).getBytes());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void twoTimeframeFunction(TaskType task) {
        Context.timeframe = task.getTimeframeIndicators(1).timeframe;
        AtomicInteger processingSymbol = new AtomicInteger(1);
        int totalSymbols = Context.symbols.size();
        Context.symbols.forEach(symbol -> {
            SymbolData screen1 = getSymbolData(task.getTimeframeIndicators(1), symbol);
            SymbolData screen2 = getSymbolData(task.getTimeframeIndicators(2), symbol);
            Log.addDebugLine("");
            Log.addDebugLine(" === " + symbol + " === ");
            try {
                System.out.println("[" + processingSymbol.get() + "/" + totalSymbols + "] Applying " + task.name() + " on " + symbol + " ...");
                TaskResult taskResult = task.getFunction().apply(screen1, screen2);
                Log.addDebugLine(taskResult.isSignal() ? "Вердикт: проверить" : "Вердикт: точно нет");
                Log.addDebugLine("");
                if (taskResult.isSignal()) {
                    Log.addLine(symbol);
                    Signal signal = new Signal();
                    signal.timeframe1 = screen1.timeframe;
                    signal.timeframe2 = screen2.timeframe;
                    signal.symbol = symbol;
                    signal.task = task;
                    signals.add(signal);
                }
            } catch (Exception e) {
                System.out.println("Function failed for symbol " + symbol + " with message: " + e.getMessage());
            }
            processingSymbol.getAndIncrement();
            QuotesInMemoryCache.clear();
            IndicatorsInMemoryCache.clear();
        });
    }

    private static void singleTimeframeFunction(Function<SymbolData, Boolean> function, Indicator... indicators) {
        try (Stream<Path> paths = Files.walk(Paths.get(CacheReader.getFolder()))) {
            paths
                    .filter(Files::isRegularFile)
                    .filter(file -> file.getFileName().toString().contains("quotes"))
                    .forEach(file -> {
                        try {
                            String symbol = file.getFileName().toString().replace("_quotes.txt", "");
                            if (!Context.testOnly.isEmpty() && !Context.testOnly.contains(symbol)) {
                                return;
                            }
                            SymbolData symbolData = new SymbolData();
                            symbolData.timeframe = Context.timeframe;
                            List<Quote> quotes = loadQuotesFromDiskCache(symbol);
                            symbolData.quotes = quotes;
                            Arrays.stream(indicators).forEach(indicator -> symbolData.indicators.put(MACD, calculateIndicatorFromCachedQuotes(symbol, quotes, MACD)));
                            Log.addDebugLine("");
                            Log.addDebugLine(" === " + symbol + " === ");
                            try {
                                Boolean signal = function.apply(symbolData);
                                Log.addDebugLine(signal ? "Вердикт: проверить" : "Вердикт: точно нет");
                                Log.addDebugLine("");
                                if (signal) {
                                    Log.addLine(symbol);
                                }
                            } catch (Exception e) {
                                System.out.println("Function failed for symbol " + symbol + " with message: " + e.getMessage());
                            }
                            symbolData.quotes.clear();
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void saveLog(TaskType task) {
        File d = new File(Context.outputFolder + "/debug");
        if (!d.exists()) {
            d.mkdir();
        }
        File s = new File(Context.outputFolder + "/signal");
        if (!s.exists()) {
            s.mkdir();
        }
        String prefix = "[" + Context.logTimeframe1.name() + "][" + Context.logTimeframe2.name() + "]";
        String customDebugFolder = Context.outputFolder + "/debug/" + prefix + task.name().toLowerCase();
        File d2 = new File(customDebugFolder);
        if (!d2.exists()) {
            d2.mkdir();
        }
        Log.saveSignal(Context.outputFolder + "/signal/" + prefix + task.name().toLowerCase() + ".txt");
        Log.saveDebug(customDebugFolder + "/all.txt");
        Log.saveCodes(customDebugFolder);
        Log.clear();
    }

    public static class Signal {
        public String symbol;
        public Timeframe timeframe1;
        public Timeframe timeframe2;
        public TaskType task;
    }

}
