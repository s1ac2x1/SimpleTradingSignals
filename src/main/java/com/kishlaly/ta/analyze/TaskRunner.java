package com.kishlaly.ta.analyze;

import com.kishlaly.ta.analyze.tasks.blocks.groups.BlocksGroupJava;
import com.kishlaly.ta.analyze.testing.HistoricalTesting;
import com.kishlaly.ta.analyze.testing.sl.StopLossStrategy;
import com.kishlaly.ta.analyze.testing.tp.TakeProfitStrategy;
import com.kishlaly.ta.cache.CacheReader;
import com.kishlaly.ta.cache.IndicatorsInMemoryCacheJava;
import com.kishlaly.ta.cache.QuotesInMemoryCache;
import com.kishlaly.ta.model.*;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.utils.*;
import org.apache.commons.io.FileUtils;

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

import static com.kishlaly.ta.analyze.testing.TaskTester.test;
import static com.kishlaly.ta.cache.CacheBuilder.getSLStrategies;
import static com.kishlaly.ta.cache.CacheBuilder.getTPStrategies;
import static com.kishlaly.ta.cache.CacheReader.*;
import static com.kishlaly.ta.model.indicators.IndicatorJava.MACD;
import static com.kishlaly.ta.utils.ContextJava.TRIM_DATA;
import static com.kishlaly.ta.utils.Quotes.resolveMinBarsCount;

/**
 * @author Vladimir Kishlaly
 * @since 18.11.2021
 */
public class TaskRunner {

    public static List<Signal> signals = new ArrayList<>();

    public static void run(TimeframeJava[][] timeframes, TaskType task, boolean findOptimal, BlocksGroupJava... blocksGroups) {
        Arrays.stream(timeframes).forEach(screens -> {
            task.updateTimeframeForScreen(1, screens[0]);
            task.updateTimeframeForScreen(2, screens[1]);
            ContextJava.logTimeframe1 = screens[0];
            ContextJava.logTimeframe2 = screens[1];
            twoTimeframeFunction(task, blocksGroups);
            System.out.println("\n");
            saveLog(task);
        });
        if (findOptimal) {
            QuotesInMemoryCache.clear();
            IndicatorsInMemoryCacheJava.clear();
            System.gc();
            //findOptimalSLTP(blocksGroups);
        }
    }

    public static void runBest(TimeframeJava[][] timeframes) {
        try {
            FileUtils.deleteDirectory(new File(ContextJava.outputFolder + "/debug"));
            FileUtils.deleteDirectory(new File(ContextJava.outputFolder + "/signal"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String tf1 = timeframes[0][0].name().toLowerCase();
        String tf2 = timeframes[0][1].name().toLowerCase();
        StringBuilder content = new StringBuilder();
        Arrays.stream(ContextJava.source).forEach(source -> {
            try {
                String best = new String(Files.readAllBytes(Paths.get("best_" + source.name().toLowerCase() + "_" + tf1 + "_" + tf2 + ".txt")));
                content.append(best).append(System.lineSeparator());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        String[] lines = content.toString().split(System.lineSeparator());
        Arrays.stream(lines).forEach(line -> {
            String[] split = line.split("=");
            String symbol = split[0];
            TaskType task = TaskType.valueOf(split[1].toUpperCase());
            ContextJava.symbols = new HashSet<String>() {{
                add(symbol);
            }};
            //run(timeframes, new TaskType[]{task}, false); TODO
        });
    }

    private static void findOptimalSLTP(BlocksGroupJava blocksGroup) {
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
                    ContextJava.stopLossStrategy = stopLossStrategy;
                    ContextJava.takeProfitStrategy = takeProfitStrategy;
                    TimeframeJava[][] timeframes = {
                            {signal.timeframe1, signal.timeframe2},
                    };
                    System.out.println("Testing symbol " + symbolNumber.get() + "/" + totalSymbols + " with TP/SL " + testingStrategySet.get() + "/" + totalSLTPStrategies);
                    ContextJava.symbols = new HashSet<String>() {{
                        add(signal.symbol);
                    }};
                    result.addAll(test(timeframes, signal.task, blocksGroup));
                    testingStrategySet.getAndIncrement();
                });
            });
            SymbolDataJava screen2 = getSymbolData(signal.task.getTimeframeIndicators(2), signal.symbol);
            Collections.sort(result, Comparator.comparing(HistoricalTesting::getBalance));
            HistoricalTesting best = result.get(result.size() - 1);
            double stopLoss = best.getStopLossStrategy().calculate(screen2, screen2.quotes.size() - 1);
            double takeProfit = best.getTakeProfitStrategy().calcualte(screen2, screen2.quotes.size() - 1);
            suggestions.add(signal.symbol + System.lineSeparator() + "SL: " + NumbersJava.round(stopLoss) + "; TP: " + NumbersJava.round(takeProfit) + System.lineSeparator() + best.getStopLossStrategy() + System.lineSeparator() + best.getTakeProfitStrategy());
            symbolNumber.getAndIncrement();
        });
        if (!suggestions.isEmpty()) {
            try {
                String prefix = "[" + ContextJava.logTimeframe1.name() + "][" + ContextJava.logTimeframe2.name() + "]";
                String fileName = ContextJava.outputFolder + "/signal/optimal.txt";
                Files.write(Paths.get(fileName), suggestions.stream().collect(Collectors.joining(System.lineSeparator() + System.lineSeparator())).getBytes());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void twoTimeframeFunction(TaskType task, BlocksGroupJava... blocksGroups) {
        ContextJava.timeframe = task.getTimeframeIndicators(1).timeframe;
        AtomicInteger processingSymbol = new AtomicInteger(1);
        int totalSymbols = ContextJava.symbols.size();
        ContextJava.symbols.forEach(symbol -> {
            SymbolDataJava screen1 = getSymbolData(task.getTimeframeIndicators(1), symbol);
            SymbolDataJava screen2 = getSymbolData(task.getTimeframeIndicators(2), symbol);

            // skip symbols if they have less than resolveMinBarsCount quotes on the weekly frame
            if (screen1.quotes.size() < resolveMinBarsCount(screen1.timeframe)
                    || screen2.quotes.size() < resolveMinBarsCount(screen2.timeframe)) {
                screen1.clear();
                screen2.clear();
                return;
            }

            if (TRIM_DATA) {
                Quotes.trim(screen1);
                Quotes.trim(screen2);
                IndicatorUtilsJava.trim(screen1);
                IndicatorUtilsJava.trim(screen2);
            }

            if (ContextJava.lowPricesOnly && screen2.quotes.get(screen2.quotes.size() - 1).getClose() > ContextJava.lowPriceLimit) {
                screen1.clear();
                screen2.clear();
                System.out.println("Skipped high price stock");
                return;
            }

            LogJava.addDebugLine("");
            LogJava.addDebugLine(" === " + symbol + " === ");
            try {
                Arrays.stream(blocksGroups).forEach(blocksGroup -> {
                    System.out.println("[" + processingSymbol.get() + "/" + totalSymbols + "] Applying " + task.name() + " " + blocksGroup.getClass().getSimpleName() + " on " + symbol + " ...");
                    BlockResultJava blockResult = task.getFunction().apply(new ScreensJava(screen1, screen2), blocksGroup.blocks());
                    LogJava.addDebugLine(blockResult.isOk() ? "To check" : "Nope");
                    LogJava.addDebugLine("");
                    if (blockResult.isOk()) {
                        //TODO Here can be added a run on candlestick patterns
                        LogJava.addLine(symbol);
                        Signal signal = new Signal();
                        signal.timeframe1 = screen1.timeframe;
                        signal.timeframe2 = screen2.timeframe;
                        signal.symbol = symbol;
                        signal.task = task;
                        signals.add(signal);
                        LogJava.addSummary(task.name(), blocksGroup, symbol); // TODO add a list of found candlestick patterns
                    }
                });
            } catch (Exception e) {
                System.out.println("Function failed for symbol " + symbol + " with message: " + e.getMessage());
            }
            processingSymbol.getAndIncrement();
            QuotesInMemoryCache.clear();
            IndicatorsInMemoryCacheJava.clear();
            screen1.clear();
            screen2.clear();
        });
    }

    private static void singleTimeframeFunction(Function<SymbolDataJava, Boolean> function, IndicatorJava... indicators) {
        try (Stream<Path> paths = Files.walk(Paths.get(CacheReader.getFolder()))) {
            paths
                    .filter(Files::isRegularFile)
                    .filter(file -> file.getFileName().toString().contains("quotes"))
                    .forEach(file -> {
                        try {
                            String symbol = file.getFileName().toString().replace("_quotes.txt", "");
                            if (!ContextJava.testOnly.isEmpty() && !ContextJava.testOnly.contains(symbol)) {
                                return;
                            }
                            SymbolDataJava symbolData = new SymbolDataJava();
                            symbolData.timeframe = ContextJava.timeframe;
                            List<QuoteJava> quotes = loadQuotesFromDiskCache(symbol);
                            symbolData.quotes = quotes;
                            Arrays.stream(indicators).forEach(indicator -> symbolData.indicators.put(MACD, calculateIndicatorFromCachedQuotes(symbol, MACD)));
                            LogJava.addDebugLine("");
                            LogJava.addDebugLine(" === " + symbol + " === ");
                            try {
                                Boolean signal = function.apply(symbolData);
                                LogJava.addDebugLine(signal ? "Вердикт: проверить" : "Вердикт: точно нет");
                                LogJava.addDebugLine("");
                                if (signal) {
                                    LogJava.addLine(symbol);
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
        File d = new File(ContextJava.outputFolder + "/debug");
        if (!d.exists()) {
            d.mkdir();
        }
        File s = new File(ContextJava.outputFolder + "/signal");
        if (!s.exists()) {
            s.mkdir();
        }
        String prefix = "[" + ContextJava.logTimeframe1.name() + "][" + ContextJava.logTimeframe2.name() + "][" + ContextJava.source[0].name() + "]";
        String customDebugFolder = ContextJava.outputFolder + "/debug/" + prefix + task.name().toLowerCase();
        File d2 = new File(customDebugFolder);
        if (!d2.exists()) {
            d2.mkdir();
        }
        //Log.saveSignal(Context.outputFolder + "/signal/" + prefix + task.name().toLowerCase() + ".txt");
        //Log.saveDebug(customDebugFolder + "/all.txt");
        //Log.saveCodes(customDebugFolder);
        LogJava.saveSummary(ContextJava.outputFolder + "/signal/" + prefix + task.name().toLowerCase() + ".html");
        LogJava.clear();
    }

    public static class Signal {
        public String symbol;
        public TimeframeJava timeframe1;
        public TimeframeJava timeframe2;
        public TaskType task;
    }

}
