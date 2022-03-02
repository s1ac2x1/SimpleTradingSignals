package com.kishlaly.ta;

import com.kishlaly.ta.analyze.TaskType;
import com.kishlaly.ta.analyze.testing.sl.*;
import com.kishlaly.ta.analyze.testing.tp.TakeProfitFixedKeltnerTop;
import com.kishlaly.ta.analyze.testing.tp.TakeProfitStrategy;
import com.kishlaly.ta.analyze.testing.tp.TakeProfitVolatileKeltnerTop;
import com.kishlaly.ta.model.HistoricalTesting;
import com.kishlaly.ta.model.SymbolsSource;
import com.kishlaly.ta.model.Timeframe;
import com.kishlaly.ta.utils.Context;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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

        Context.source = SymbolsSource.NAGA;
//        Context.testOnly = new ArrayList<String>() {{
//            add("PYPL");
//        }};
        Context.symbols = getSymbols();
        Context.yearsToAnalyze = 5;

        TaskType[] tasks = {
                //MACD_BULLISH_DIVERGENCE,
                //THREE_DISPLAYS_BUY, // лучше работает для DAY-HOUR
                //THREE_DISPLAYS_BUY_TYPE_2, // лучше работает для WEEK-DAY
                THREE_DISPLAYS_BUY_TYPE_4,
                //FIRST_TRUST_MODEL, // искать на S&P500
        };

//        buildCache(timeframes, tasks, false);
//        findBestStrategyForSymbols();
//        checkCache(timeframes, tasks);
//        run(timeframes, tasks, false);
        runBest(timeframes);
//        testOneStrategy(timeframes, tasks, new StopLossFixedPrice(0.27), new TakeProfitFixedKeltnerTop(100));
//        buildTasksAndStrategiesSummary(timeframes, tasks, new StopLossFixedPrice(0.27), new TakeProfitFixedKeltnerTop(100));
//        buildTasksAndStrategiesSummary(timeframes, tasks, null, null);
    }

    private static void testOneStrategy(Timeframe[][] timeframes, TaskType[] tasks, StopLossStrategy stopLossStrategy, TakeProfitStrategy takeProfitStrategy) {
        Context.stopLossStrategy = stopLossStrategy;
        Context.takeProfitStrategy = takeProfitStrategy;
        System.out.println(stopLossStrategy + " / " + takeProfitStrategy);
        test(timeframes, tasks);
    }

    private static void buildTasksAndStrategiesSummary(Timeframe[][] timeframes,
                                                       TaskType[] tasks,
                                                       StopLossStrategy stopLossStrategy,
                                                       TakeProfitStrategy takeProfitStrategy) {
        List<HistoricalTesting> result = new ArrayList<>();
        int total = getSLStrategies().size() * getTPStrategies().size();
        AtomicInteger current = new AtomicInteger(1);
        if (stopLossStrategy == null || takeProfitStrategy == null) {
            getSLStrategies().forEach(sl -> {
                getTPStrategies().forEach(tp -> {
                    Context.stopLossStrategy = sl;
                    Context.takeProfitStrategy = tp;
                    System.out.println(current.get() + "/" + total + " " + sl + " / " + tp);
                    result.addAll(test(timeframes, tasks));
                    current.getAndIncrement();
                });
            });
        } else {
            Context.stopLossStrategy = stopLossStrategy;
            Context.takeProfitStrategy = takeProfitStrategy;
            result.addAll(test(timeframes, tasks));
        }
        saveTable(result);
    }

    // для каждого набора символов (SP500, NAGA, ...) создает файл best_{set}_{scree1}_{scree2}.txt
    // в этом файле строки вида symbol=TaskType
    // подразумевается, что каждому символу соответствует TaskType, который показал лучший результат на исторических данных
    // при тестировании сигналов использовалась базовая пара StopLossFixedPrice(0.27) и TakeProfitFixedKeltnerTop(100)
    private static void findBestStrategyForSymbols() {
        Timeframe[][] timeframes = {
                {Timeframe.WEEK, Timeframe.DAY},
        };
        TaskType[] tasks = {
                THREE_DISPLAYS_BUY_TYPE_2,
                THREE_DISPLAYS_BUY_TYPE_4
        };
        List<HistoricalTesting> result = new ArrayList<>();
        Context.stopLossStrategy = new StopLossFixedPrice(0.27);
        Context.takeProfitStrategy = new TakeProfitFixedKeltnerTop(100);
        result.addAll(test(timeframes, tasks));
        Map<String, TaskType> winners = new HashMap<>();
        result.stream().collect(Collectors.groupingBy(HistoricalTesting::getSymbol))
                .entrySet().stream().forEach(bySymbol -> {
                    String symbol = bySymbol.getKey();
                    List<HistoricalTesting> testings = bySymbol.getValue();
                    Collections.sort(testings, Comparator.comparing(HistoricalTesting::getBalance));
                    HistoricalTesting best = testings.get(testings.size() - 1);
                    winners.put(symbol, best.getTaskType());
                    System.out.println(symbol + " " + best.getTaskType().name() + " " + best.getBalance());
                });
        StringBuilder builder = new StringBuilder();
        winners.entrySet().stream().forEach(entry -> {
            builder.append(entry.getKey()).append("=").append(entry.getValue().name()).append(System.lineSeparator());
        });
        writeToFile("best_" + Context.source.name().toLowerCase() + "_" + timeframes[0][0].name().toLowerCase() + "_" + timeframes[0][1].name().toLowerCase() + ".txt", builder.toString());
    }

    private static void saveTable(List<HistoricalTesting> result) {
        StringBuilder table = new StringBuilder("<table>");
        result
                .stream()
                .collect(Collectors.groupingBy(HistoricalTesting::getSymbol))
                .entrySet().stream()
                .forEach(bySymbol -> {
                    String symbol = bySymbol.getKey();
                    table.append("<tr>");
                    table.append("<td style=\"vertical-align: left;\">" + symbol + "</td>");
                    table.append("<td>");
                    StringBuilder innerTable = new StringBuilder("<table>");
                    List<HistoricalTesting> testings = bySymbol.getValue();
                    Collections.sort(testings, Comparator.comparing(HistoricalTesting::getBalance));
                    testings.stream()
                            .collect(Collectors.groupingBy(HistoricalTesting::getTaskType))
                            .entrySet().stream().forEach(byTask -> {
                                TaskType taskType = byTask.getKey();
                                List<HistoricalTesting> historicalTestings = byTask.getValue();
                                HistoricalTesting best = historicalTestings.get(historicalTestings.size() - 1);
                                innerTable.append("<tr>");
                                innerTable.append("<td style=\"vertical-align: top text-align: left;\">" + taskType.name() + "</td>");
                                innerTable.append("<td style=\"vertical-align: top text-align: center;\">&nbsp;&nbsp;&nbsp;</td>");

                                innerTable.append("<td style=\"vertical-align: top text-align: left; white-space: nowrap;\">" + best.printTPSLNumber() + "</td>");
                                innerTable.append("<td style=\"vertical-align: top text-align: center;\">&nbsp;&nbsp;&nbsp;</td>");

                                innerTable.append("<td style=\"vertical-align: top text-align: left; white-space: nowrap;\">" + best.printTPSLPercent() + "</td>");
                                innerTable.append("<td style=\"vertical-align: top text-align: center;\">&nbsp;&nbsp;&nbsp;</td>");

                                innerTable.append("<td style=\"vertical-align: top text-align: left;\">" + best.getBalance() + "</td>");
                                innerTable.append("<td style=\"vertical-align: top text-align: center;\">&nbsp;&nbsp;&nbsp;</td>");

                                innerTable.append("<td style=\"vertical-align: top text-align: left; white-space: nowrap;\">" + best.getStopLossStrategy() + "</td>");
                                innerTable.append("<td style=\"vertical-align: top text-align: center;\">&nbsp;&nbsp;&nbsp;</td>");

                                innerTable.append("<td style=\"vertical-align: top text-align: left; white-space: nowrap;\">" + best.getTakeProfitStrategy() + "</td>");
                                innerTable.append("<td style=\"vertical-align: top text-align: center;\">&nbsp;&nbsp;&nbsp;</td>");

                                innerTable.append("</tr>");
                            });
                    innerTable.append("</table>");
                    table.append(innerTable);
                    table.append("</td>");
                    table.append("</tr>");
                });
        table.append("</table>");
        writeToFile("tests/table.html", table.toString());
    }

    public static void writeToFile(String name, String content) {
        try {
            Files.write(Paths.get(name), content.toString().getBytes());
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
            add(new StopLossVolatileKeltnerBottom(80));
            add(new StopLossVolatileKeltnerBottom(100));
            add(new StopLossVolatileLocalMin(0.27));
            add(new StopLossVolatileATR());
        }};
    }

    public static List<TakeProfitStrategy> getTPStrategies() {
        return new ArrayList<TakeProfitStrategy>() {{
            add(new TakeProfitFixedKeltnerTop(80));
            add(new TakeProfitFixedKeltnerTop(100));
            add(new TakeProfitVolatileKeltnerTop(80));
            add(new TakeProfitVolatileKeltnerTop(100));
        }};
    }

}