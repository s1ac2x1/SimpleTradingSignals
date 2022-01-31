package com.kishlaly.ta.analyze;

import com.kishlaly.ta.cache.CacheReader;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.TaskResult;
import com.kishlaly.ta.model.Timeframe;
import com.kishlaly.ta.model.TimeframeIndicators;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.utils.Context;
import com.kishlaly.ta.utils.Log;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.kishlaly.ta.cache.CacheReader.*;
import static com.kishlaly.ta.model.indicators.Indicator.MACD;
import static com.kishlaly.ta.utils.Clean.clear;

/**
 * @author Vladimir Kishlaly
 * @since 18.11.2021
 */
public class TaskRunner {

    public static void run(Timeframe[][] timeframes, TaskType[] tasks) {
        Arrays.stream(timeframes).forEach(screens -> Arrays.stream(tasks).forEach(task -> {
            task.updateTimeframeForScreen(1, screens[0]);
            task.updateTimeframeForScreen(2, screens[1]);
            Context.logTimeframe1 = screens[0];
            Context.logTimeframe2 = screens[1];
            twoTimeframeFunction(task.getFunction(),
                    task.getTimeframeIndicators(1),
                    task.getTimeframeIndicators(2));
            saveLog(task);
        }));
    }

    private static void twoTimeframeFunction(BiFunction<SymbolData, SymbolData, TaskResult> function,
                                             TimeframeIndicators longTerm,
                                             TimeframeIndicators middleTerm) {
        List<String> symbols = new ArrayList<>(getSymbols());
        Context.timeframe = longTerm.timeframe;
        symbols.forEach(symbol -> {
            SymbolData screen1 = getSymbolData(longTerm, symbol);
            SymbolData screen2 = getSymbolData(middleTerm, symbol);
            Log.addDebugLine("");
            Log.addDebugLine(" === " + symbol + " === ");
            try {
                TaskResult taskResult = function.apply(screen1, screen2);
                Log.addDebugLine(taskResult.isSignal() ? "Вердикт: проверить" : "Вердикт: точно нет");
                Log.addDebugLine("");
                if (taskResult.isSignal()) {
                    Log.addLine(symbol);
                }
            } catch (Exception e) {
                System.out.println("Function failed for symbol " + symbol + " with message: " + e.getMessage());
            }
            clear(screen1);
            clear(screen2);
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
                            symbolData.quotes = loadQuotesFromCache(symbol);
                            Arrays.stream(indicators).forEach(indicator -> symbolData.indicators.put(MACD, calculateIndicatorFromCachedQuotes(symbol, MACD)));
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

    private static void saveLog(TaskType type) {
        File d = new File(Context.outputFolder + "/debug");
        if (!d.exists()) {
            d.mkdir();
        }
        File s = new File(Context.outputFolder + "/signal");
        if (!s.exists()) {
            s.mkdir();
        }
        String prefix = "[" + Context.logTimeframe1.name() + "][" + Context.logTimeframe2.name() + "]";
        String customDebugFolder = Context.outputFolder + "/debug/" + prefix + type.name().toLowerCase();
        File d2 = new File(customDebugFolder);
        if (!d2.exists()) {
            d2.mkdir();
        }
        Log.saveSignal(Context.outputFolder + "/signal/" + prefix + type.name().toLowerCase() + ".txt");
        Log.saveDebug(customDebugFolder + "/all.txt");
        Log.saveCodes(customDebugFolder);
        Log.clear();
    }

}
