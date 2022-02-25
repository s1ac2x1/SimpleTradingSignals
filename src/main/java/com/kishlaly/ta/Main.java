package com.kishlaly.ta;

import com.kishlaly.ta.analyze.TaskType;
import com.kishlaly.ta.analyze.testing.TaskTester;
import com.kishlaly.ta.analyze.testing.sl.*;
import com.kishlaly.ta.analyze.testing.tp.TakeProfitDisabled;
import com.kishlaly.ta.analyze.testing.tp.TakeProfitFixedKeltnerTop;
import com.kishlaly.ta.analyze.testing.tp.TakeProfitStrategy;
import com.kishlaly.ta.analyze.testing.tp.TakeProfitVolatileKeltnerTop;
import com.kishlaly.ta.model.HistoricalTesting;
import com.kishlaly.ta.model.Timeframe;
import com.kishlaly.ta.utils.Context;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.kishlaly.ta.analyze.TaskRunner.run;
import static com.kishlaly.ta.analyze.TaskType.*;
import static com.kishlaly.ta.analyze.testing.TaskTester.test;
import static com.kishlaly.ta.cache.CacheBuilder.buildCache;
import static com.kishlaly.ta.cache.CacheReader.getSymbols;

/**
 * @author Vladimir Kishlaly
 * @since 15.11.2021
 */
public class Main {

    public static void main(String[] args) throws Exception {

        Context.aggregationTimeframe = Timeframe.DAY;

        Timeframe[][] timeframes = {
                {Timeframe.WEEK, Timeframe.DAY},
//                {Timeframe.DAY, Timeframe.HOUR},
        };

        Context.source = "symbols/sp500.txt";
//        Context.source = "symbols/screener_2.txt";
//        Context.source = "symbols/screener_many.txt";
//        Context.source = "symbols/naga.txt";
        Context.testOnly = new ArrayList<String>() {{
            add("LMT");
        }};
        Context.symbols = getSymbols();
        Context.yearsToAnalyze = 5;

        TaskType[] tasks = {
                //MACD_BULLISH_DIVERGENCE,
                //THREE_DISPLAYS_BUY, // лучше работает для DAY-HOUR
                //THREE_DISPLAYS_BUY_TYPE2, // лучше работает для WEEK-DAY
                THREE_DISPLAYS_BUY_TYPE4,
                //FIRST_TRUST_MODEL, // искать на S&P500
        };

//        buildCache(timeframes, tasks, false);
//        checkCache(timeframes, tasks);
//        run(timeframes, tasks);
//        testOneStrategy(timeframes, tasks, new StopLossFixedPrice(0.27), new TakeProfitVolatileKeltnerTop(100));
        testAllStrategies(timeframes, tasks);
    }

    private static void testOneStrategy(Timeframe[][] timeframes, TaskType[] tasks, StopLossStrategy stopLossStrategy, TakeProfitStrategy takeProfitStrategy) {
        Context.stopLossStrategy = stopLossStrategy;
        Context.takeProfitStrategy = takeProfitStrategy;
        System.out.println(stopLossStrategy + " / " + takeProfitStrategy);
        test(timeframes, tasks);
    }

    private static void testAllStrategies(Timeframe[][] timeframes, TaskType[] tasks) {
        List<HistoricalTesting> result = new ArrayList<>();
        int total = getSLStrategies().size() * getTPStrategies().size();
        AtomicInteger current = new AtomicInteger(1);
        getSLStrategies().forEach(stopLossStrategy -> {
            getTPStrategies().forEach(takeProfitStrategy -> {
                Context.stopLossStrategy = stopLossStrategy;
                Context.takeProfitStrategy = takeProfitStrategy;
                System.out.println(current.get() + "/" + total + " " + stopLossStrategy + " / " + takeProfitStrategy);
                result.addAll(test(timeframes, tasks));
                current.getAndIncrement();
            });
        });
        Collections.sort(result, Comparator.comparing(HistoricalTesting::getBalance));
        HistoricalTesting worse = result.get(0);
        writeToFile(worse.getData().symbol + "_worse", TaskTester.formatTestingSummary(worse));
        HistoricalTesting best = result.get(result.size() - 1);
        writeToFile(best.getData().symbol + "_best", TaskTester.formatTestingSummary(best));
    }

    public static void writeToFile(String name, String content) {
        try {
            Files.write(Paths.get("tests/" + name + ".txt"), content.toString().getBytes());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void testMass(Timeframe[][] timeframes, TaskType[] tasks) {
        Context.massTesting = true;

        StopLossStrategy stopLossStrategy = new StopLossFixedPrice(0.27);
        Context.stopLossStrategy = stopLossStrategy;

        Context.takeProfitStrategies = new ArrayList<>();
        for (int i = 80; i <= 100; i++) {
            TakeProfitStrategy tp = new TakeProfitFixedKeltnerTop(i);
            Context.takeProfitStrategies.add(tp);
        }
        test(timeframes, tasks);
    }

    public static List<StopLossStrategy> getSLStrategies() {
        return new ArrayList<StopLossStrategy>() {{
            add(new StopLossFixedPrice(0.27));
            add(new StopLossFixedKeltnerBottom());
            add(new StopLossVolatileKeltnerBottom(70));
            add(new StopLossVolatileKeltnerBottom(80));
            add(new StopLossVolatileKeltnerBottom(100));
            add(new StopLossVolatileLocalMin(0.27));
            add(new StopLossVolatileATR());
        }};
    }

    public static List<TakeProfitStrategy> getTPStrategies() {
        return new ArrayList<TakeProfitStrategy>() {{
            add(new TakeProfitFixedKeltnerTop(70));
            add(new TakeProfitFixedKeltnerTop(80));
            add(new TakeProfitFixedKeltnerTop(90));
            add(new TakeProfitFixedKeltnerTop(100));
            add(new TakeProfitVolatileKeltnerTop(70));
            add(new TakeProfitVolatileKeltnerTop(80));
            add(new TakeProfitVolatileKeltnerTop(90));
            add(new TakeProfitVolatileKeltnerTop(100));
        }};
    }

}