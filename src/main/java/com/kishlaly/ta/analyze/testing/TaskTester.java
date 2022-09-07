package com.kishlaly.ta.analyze.testing;

import com.kishlaly.ta.analyze.TaskType;
import com.kishlaly.ta.analyze.tasks.blocks.groups.BlockGroupsUtils;
import com.kishlaly.ta.analyze.tasks.blocks.groups.BlocksGroupJava;
import com.kishlaly.ta.analyze.testing.sl.StopLossFixedPrice;
import com.kishlaly.ta.analyze.testing.sl.StopLossStrategy;
import com.kishlaly.ta.analyze.testing.tp.TakeProfitFixedKeltnerTop;
import com.kishlaly.ta.analyze.testing.tp.TakeProfitStrategy;
import com.kishlaly.ta.cache.IndicatorsInMemoryCacheJava;
import com.kishlaly.ta.cache.QuotesInMemoryCache;
import com.kishlaly.ta.model.*;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.utils.ContextJava;
import com.kishlaly.ta.utils.FileUtilsJava;
import com.kishlaly.ta.utils.NumbersJava;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kishlaly.ta.cache.CacheReader.getSymbolData;
import static com.kishlaly.ta.analyze.testing.HistoricalTesting.PositionTestResult;
import static com.kishlaly.ta.model.QuoteJava.exchangeTimezome;
import static com.kishlaly.ta.utils.ContextJava.*;
import static com.kishlaly.ta.utils.DatesJava.getBarTimeInMyZone;
import static com.kishlaly.ta.utils.DatesJava.shortDateToZoned;
import static com.kishlaly.ta.utils.Quotes.resolveMinBarsCount;
import static java.lang.System.lineSeparator;

public class TaskTester {

    private static StringBuilder testLog = new StringBuilder();

    public static List<HistoricalTesting> test(TimeframeJava[][] timeframes, TaskType task, BlocksGroupJava blocksGroup) {
        ContextJava.testMode = true;
        StringBuilder log = new StringBuilder();
        List<HistoricalTesting> allTests = new ArrayList<>();
        Arrays.stream(timeframes).forEach(screens -> {
            task.updateTimeframeForScreen(1, screens[0]);
            task.updateTimeframeForScreen(2, screens[1]);
            Map<String, Set<String>> readableOutput = new HashMap<>();
            AtomicInteger currSymbol = new AtomicInteger(1);
            int totalSymbols = ContextJava.symbols.size();
            ContextJava.symbols.forEach(symbol -> {
                System.out.println("[" + currSymbol + "/" + totalSymbols + "] Testing " + symbol);
                currSymbol.getAndIncrement();
                SymbolDataJava screen1 = getSymbolData(task.getTimeframeIndicators(1), symbol);
                SymbolDataJava screen2 = getSymbolData(task.getTimeframeIndicators(2), symbol);
                SymbolDataJava symbolDataForTesting = getSymbolData(task.getTimeframeIndicators(2), symbol);
                List<BlockResultJava> blockResults = new ArrayList<>();
                if (!isDataFilled(screen1, screen2)) {
                    return;
                }
                try {
                    while (hasHistory(screen1, screen2)) {
                        rewind(task, blocksGroup, screen1, screen2, blockResults);
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                if (!blockResults.isEmpty()) {
                    HistoricalTesting testing = null;
                    if (ContextJava.massTesting) {
                        if (ContextJava.takeProfitStrategies != null) {
                            ContextJava.takeProfitStrategies.forEach(takeProfitStrategy -> {
                                HistoricalTesting massTesting = new HistoricalTesting(task, blocksGroup, symbolDataForTesting, blockResults, ContextJava.stopLossStrategy, takeProfitStrategy);
                                calculateStatistics(massTesting);
                                allTests.add(massTesting);
                            });
                        }
                    } else {
                        testing = new HistoricalTesting(task, blocksGroup, symbolDataForTesting, blockResults, ContextJava.stopLossStrategy, ContextJava.takeProfitStrategy);
                        calculateStatistics(testing);
                        allTests.add(testing);
                        String key = "[" + screens[0].name() + "][" + screens[1] + "] " + task.name() + " - " + symbol;
                        Set<String> signalResults = readableOutput.get(key);
                        if (signalResults == null) {
                            signalResults = new LinkedHashSet<>();
                        }
                        signalResults.add(formatTestingSummary(testing));
                        Set<String> finalSignalResults = signalResults;

                        // at this stage HistoricalTesting contains tests of positions by signals
                        // as well as all failure results
                        // TaskResult.lastChartQuote can be null if the strategy did not have enough quotes for the test

                        // first print the item report
                        printPositionsReport(screen2.timeframe, testing, finalSignalResults);

                        // then a log of all other quotes with the reason why the strategy failed
                        printNoSignalsReport(screen2.timeframe, testing, finalSignalResults);

                        readableOutput.put(key, signalResults);
                    }
                }

                // hint for GC
                clean(screen1, screen2);
            });
            if (!ContextJava.massTesting) {
                readableOutput.forEach((key, data) -> {
                    data.forEach(line -> log.append("    " + line).append(lineSeparator()));
                    log.append(lineSeparator());
                });
            }
        });
        File directory = new File(TESTS_FOLDER);
        if (!directory.exists()) {
            directory.mkdir();
        }
        if (!ContextJava.massTesting) {
            try {
                Files.write(Paths.get(TESTS_FOLDER + fileSeparator + SINGLE_TXT), log.toString().getBytes());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } else {
            StringBuilder builder = new StringBuilder();
            allTests.forEach(testing -> {
                if (ContextJava.takeProfitStrategies != null) {
                    builder.append("TP: " + testing.printTP() + " => TP/SL = " + testing.printTPSLNumber() + " (" + testing.printTPSLPercent() + "); balance = " + testing.getBalance()).append(lineSeparator());
                }
            });
            try {
                Files.write(Paths.get(TESTS_FOLDER + fileSeparator + MASS_TXT), builder.toString().getBytes());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        return allTests;
    }

    private static void clean(SymbolDataJava screen1, SymbolDataJava screen2) {
        QuotesInMemoryCache.clear();
        IndicatorsInMemoryCacheJava.clear();
        screen1.quotes.clear();
        screen1.indicators.clear();
        screen2.quotes.clear();
        screen2.indicators.clear();
    }

    private static void rewind(TaskType task, BlocksGroupJava blocksGroup, SymbolDataJava screen1, SymbolDataJava screen2, List<BlockResultJava> blockResults) {
        QuoteJava lastScreen1Quote = screen1.quotes.get(screen1.quotes.size() - 1);
        QuoteJava lastScreen2Quote = screen2.quotes.get(screen2.quotes.size() - 1);
        blockResults.add(task.getFunction().apply(new ScreensJava(screen1, screen2), blocksGroup.blocks()));
        if (lastScreen2Quote.getTimestamp() < lastScreen1Quote.getTimestamp()) {
            rewind(screen1, 1);
        } else {
            rewind(screen2, 1);
        }
    }

    public static void printNoSignalsReport(TimeframeJava timeframe, HistoricalTesting testing, Set<String> finalSignalResults) {
        finalSignalResults.add(lineSeparator());
        finalSignalResults.add(lineSeparator());
        finalSignalResults.add(lineSeparator());
        testing.getTaskResults()
                .stream()
                .filter(taskResult -> taskResult.getLastChartQuote() != null)
                .filter(taskResult -> !taskResult.isOk())
                .forEach(taskResult -> {
                    String quoteDateFormatted = formatDate(timeframe, taskResult.getLastChartQuote().getTimestamp());
                    finalSignalResults.add(quoteDateFormatted + " ### " + taskResult.getCode());
                });
    }

    public static void printPositionsReport(TimeframeJava timeframe, final HistoricalTesting testing, final Set<String> report) {
        testing.getTaskResults()
                .stream()
                .filter(taskResult -> taskResult.getLastChartQuote() != null)
                .filter(taskResult -> taskResult.isOk())
                .forEach(taskResult -> {
                    String line = "";
                    QuoteJava quote = taskResult.getLastChartQuote();
                    String quoteDateFormatted = formatDate(timeframe, quote.getTimestamp());

                    // результаты тестирования сигналов
                    if (taskResult.isOk()) {
                        PositionTestResult positionTestResult = testing.getResult(quote);
                        if (!positionTestResult.isClosed()) {
                            line += " NOT CLOSED";
                        } else {
                            line += positionTestResult.isProfitable() ? "PROFIT " : "LOSS ";
                            line += positionTestResult.getRoi() + "%";
                            if (positionTestResult.isGapUp()) {
                                line += " (gap up)";
                            }
                            if (positionTestResult.isGapDown()) {
                                line += " (gap down)";
                            }
                            line += " " + positionTestResult.getPositionDuration(timeframe);
                            String endDate = getBarTimeInMyZone(positionTestResult.getClosedTimestamp(), exchangeTimezome).toString();
                            ZonedDateTime parsed = ZonedDateTime.parse(endDate);
                            String parsedEndDate = parsed.getDayOfMonth() + " " + parsed.getMonth() + " " + parsed.getYear();
                            if (timeframe == TimeframeJava.HOUR) {
                                parsedEndDate += " " + parsed.getHour() + ":" + parsed.getMinute();
                            }
                            line += " [till " + parsedEndDate + "]";
                        }
                        report.add(quoteDateFormatted + " --- " + line);
                    }
                });
    }

    private static String formatDate(TimeframeJava timeframe, long timestamp) {
        String date = getBarTimeInMyZone(timestamp, exchangeTimezome).toString();
        ZonedDateTime parsedDate = ZonedDateTime.parse(date);
        date = parsedDate.getDayOfMonth() + " " + parsedDate.getMonth() + " " + parsedDate.getYear();
        if (timeframe == TimeframeJava.HOUR) {
            date += " " + parsedDate.getHour() + ":" + parsedDate.getMinute();
        }
        return date;
    }

    public static String formatTestingSummary(HistoricalTesting testing) {
        String timeframesInfo = "[" + testing.getTaskType().getTimeframeForScreen(1) + "][" + testing.getTaskType().getTimeframeForScreen(2) + "]";
        String result = timeframesInfo + " " + testing.getData().symbol + " - " + testing.getTaskType().name() + " - " + testing.getBlocksGroup().getClass().getSimpleName() + lineSeparator();
        result += "\ttrendCheckIncludeHistogram = " + ContextJava.trendCheckIncludeHistogram + lineSeparator();
        result += "\teach trade size = $" + accountBalance + lineSeparator();
        result += "\t" + testing.printSL() + lineSeparator();
        result += "\t" + testing.printTP() + lineSeparator();
        result += "\tTP/SL = " + testing.printTPSLNumber() + " = ";
        result += testing.printTPSLPercent() + lineSeparator();
        double balance = testing.getBalance();
        result += "\tTotal profit after " + ContextJava.tradeCommission + "% commissions per trade = " + NumbersJava.round(balance) + lineSeparator();
        result += "\tTotal profit / loss = " + testing.getTotalProfit() + " / " + testing.getTotalLoss() + lineSeparator();
        long minPositionDurationSeconds = testing.getMinPositionDurationSeconds();
        long maxPositionDurationSeconds = testing.getMaxPositionDurationSeconds();
        String longestPositionRange = formatRange(testing, t -> t.searchSignalByLongestPosition());
        long averagePositionDurationSeconds = testing.getAveragePositionDurationSeconds();
        switch (testing.getData().timeframe) {
            case DAY:
                result += "\tmin duration = " + (int) TimeUnit.SECONDS.toDays(minPositionDurationSeconds) + " days" + lineSeparator();
                result += "\tmax duration = " + (int) TimeUnit.SECONDS.toDays(maxPositionDurationSeconds) + " days " + longestPositionRange + lineSeparator();
                result += "\tavg duration = " + (int) TimeUnit.SECONDS.toDays(averagePositionDurationSeconds) + " days" + lineSeparator();
                break;
            case HOUR:
                result += "\tmin duration = " + (int) TimeUnit.SECONDS.toHours(minPositionDurationSeconds) + " hours" + lineSeparator();
                result += "\tmax duration = " + (int) TimeUnit.SECONDS.toHours(maxPositionDurationSeconds) + " hours" + lineSeparator(); // TODO сюда диапазон
                result += "\tavg duration = " + (int) TimeUnit.SECONDS.toHours(averagePositionDurationSeconds) + " hours" + lineSeparator();
                break;
        }
        if (testing.searchSignalByProfit(testing.getMinProfit()) != null) {
            result += "\tmin profit = " + testing.searchSignalByProfit(testing.getMinProfit()).getRoi() + "% " + formatRange(testing, t -> t.searchSignalByProfit(t.getMinProfit())) + lineSeparator();
        }
        if (testing.searchSignalByProfit(testing.getMaxProfit()) != null) {
            result += "\tmax profit = " + testing.searchSignalByProfit(testing.getMaxProfit()).getRoi() + "% " + formatRange(testing, t -> t.searchSignalByProfit(t.getMaxProfit())) + lineSeparator();
        }
        if (testing.searchSignalByLoss(testing.getMinLoss()) != null) {
            result += "\tmin loss = " + testing.searchSignalByLoss(testing.getMinLoss()).getRoi() + "% " + formatRange(testing, t -> t.searchSignalByLoss(t.getMinLoss())) + lineSeparator();
        }
        if (testing.searchSignalByLoss(testing.getMaxLoss()) != null) {
            result += "\tmax loss = " + testing.searchSignalByLoss(testing.getMaxLoss()).getRoi() + "% " + formatRange(testing, t -> t.searchSignalByLoss(t.getMaxLoss())) + lineSeparator();
        }
        result += "\tavg profit / loss = " + testing.getAvgProfit() + " / " + testing.getAvgLoss() + lineSeparator();
        return result;
    }

    private static String formatRange(HistoricalTesting testing, Function<HistoricalTesting, PositionTestResult> function) {
        PositionTestResult positionTestResult = function.apply(testing);
        String output = "";
        if (positionTestResult != null) {
            output = "[" + formatDate(testing.getData().timeframe, positionTestResult.getOpenedTimestamp()) + " - " + formatDate(testing.getData().timeframe, positionTestResult.getClosedTimestamp()) + "]";
        }
        return output;
    }

    public static void testOneStrategy(TimeframeJava[][] timeframes, TaskType task, BlocksGroupJava blocksGroup, StopLossStrategy stopLossStrategy, TakeProfitStrategy takeProfitStrategy) {
        ContextJava.stopLossStrategy = stopLossStrategy;
        ContextJava.takeProfitStrategy = takeProfitStrategy;
        System.out.println(stopLossStrategy + " / " + takeProfitStrategy);
        test(timeframes, task, blocksGroup);
    }

    public static void testAllStrategiesOnSpecificDate(String datePart, TaskType task, TimeframeJava[][] timeframes) {
        if (ContextJava.symbols.size() > 1) {
            throw new RuntimeException("Only one symbol allowed here");
        }
        // SL/TP are not important here, it is important what signal or error code in a particular date
        ContextJava.stopLossStrategy = new StopLossFixedPrice(0.27);
        ContextJava.takeProfitStrategy = new TakeProfitFixedKeltnerTop(30);
        BlocksGroupJava[] blocksGroups = BlockGroupsUtils.getAllGroups(task);
        List<HistoricalTesting> testings = Arrays.stream(blocksGroups).flatMap(blocksGroup -> test(timeframes, task, blocksGroup).stream()).collect(Collectors.toList());
        ZonedDateTime parsed = shortDateToZoned(datePart);
        testings.forEach(testing -> {
            String groupName = testing.getBlocksGroup().getClass().getSimpleName();
            BlockResultJava blockResult = testing.getTaskResults().stream().filter(taskResult -> taskResult.getLastChartQuote().getTimestamp() == parsed.toEpochSecond()).findFirst().get();
            System.out.println(datePart + " " + groupName + " = " + blockResult.getCode());
        });
    }

    public static void testMass(TimeframeJava[][] timeframes, TaskType task, BlocksGroupJava blocksGroup) {
        ContextJava.massTesting = true;

        StopLossStrategy stopLossStrategy = new StopLossFixedPrice(0.27);
        ContextJava.stopLossStrategy = stopLossStrategy;

        ContextJava.takeProfitStrategies = new ArrayList<>();
        for (int i = 80; i <= 100; i++) {
            TakeProfitStrategy tp = new TakeProfitFixedKeltnerTop(i);
            ContextJava.takeProfitStrategies.add(tp);
        }
        test(timeframes, task, blocksGroup);
    }

    /**
     * Simple testing of long positions
     * <p>
     * TODO implement inverted logic for short positions
     *
     * @param historicalTesting
     */
    private static void calculateStatistics(HistoricalTesting historicalTesting) {
        historicalTesting.getTaskResults()
                .stream()
                .filter(taskResult -> taskResult.isOk()) // take only the signals to the input
                .forEach(taskResult -> testPosition(taskResult, historicalTesting));
        testLog.append(historicalTesting.getSymbol() + lineSeparator());
        FileUtilsJava.writeToFile(outputFolder + fileSeparator + "stats" + fileSeparator + historicalTesting.getSymbol() + "_test_log.txt", testLog.toString());
    }

    private static void testPosition(BlockResultJava blockResult, HistoricalTesting historicalTesting) {
        PositionTestResult positionTestResult = new PositionTestResult();
        QuoteJava signal = blockResult.getLastChartQuote();
        int signalIndex = -1;
        SymbolDataJava data = historicalTesting.getData();
        for (int i = 0; i < data.quotes.size(); i++) {
            if (data.quotes.get(i).getTimestamp().compareTo(signal.getTimestamp()) == 0) {
                signalIndex = i;
                break;
            }
        }

        // minimal amount of quotes in the chart
        if (signalIndex > MIN_POSSIBLE_QUOTES) {

            StopLossStrategy stopLossStrategy = historicalTesting.getStopLossStrategy();
            double stopLoss = stopLossStrategy.calculate(data, signalIndex);

            TakeProfitStrategy takeProfitStrategy = historicalTesting.getTakeProfitStrategy();
            double takeProfit = takeProfitStrategy.calcualte(data, signalIndex);

            double openingPrice = signal.getClose() + 0.07;
            int lots = NumbersJava.roundDown(ContextJava.accountBalance / openingPrice);
            double openPositionSize = lots * openingPrice;
            double commissions = openPositionSize / 100 * ContextJava.tradeCommission;

            boolean skip = openingPrice > takeProfit;

            if (!skip) {
                testLog.append("signal " + signal.getNativeDate() + lineSeparator());
                testLog.append("\tSL: " + NumbersJava.round(stopLoss) + lineSeparator());
                testLog.append("\tTP: " + NumbersJava.round(takeProfit) + lineSeparator());
                testLog.append("\topen price: " + NumbersJava.round(openingPrice) + lineSeparator());
            } else {
                testLog.append("signal " + signal.getNativeDate() + " SKIPPED");
            }

            int startPositionIndex = signalIndex;
            double profit = 0;
            double loss = 0;
            boolean profitable = false;
            boolean caughtGapUp = false;
            boolean caughtGapDown = false;
            double roi = 0;
            double closePositionPrice = 0;
            double closePositionCost = 0;
            QuoteJava closePositionQuote = null;

            while (!skip && startPositionIndex < data.quotes.size() - 1) {
                startPositionIndex++;
                QuoteJava nextQuote = data.quotes.get(startPositionIndex);
                boolean tpInsideBar = takeProfitStrategy.isEnabled()
                        ? nextQuote.getLow() < takeProfit && nextQuote.getHigh() > takeProfit
                        : false;
                boolean tpAtHigh = takeProfitStrategy.isEnabled()
                        ? nextQuote.getHigh() == takeProfit
                        : false;
                boolean gapUp = takeProfitStrategy.isEnabled()
                        ? nextQuote.getOpen() > takeProfit
                        : false;

                // closed on TP
                if (tpInsideBar || tpAtHigh || gapUp) {
                    if (gapUp) {
                        takeProfit = nextQuote.getOpen();
                    }
                    double closingPositionSize = lots * takeProfit;
                    profit = closingPositionSize - openPositionSize;
                    roi = NumbersJava.roi(openPositionSize, closingPositionSize);
                    profitable = true;
                    closePositionQuote = nextQuote;
                    caughtGapUp = gapUp;
                    closePositionPrice = takeProfit;
                    closePositionCost = closePositionPrice;
                    break;
                }
                boolean slInsideBar = nextQuote.getLow() < stopLoss && nextQuote.getHigh() > stopLoss;
                boolean slAtLow = nextQuote.getLow() == stopLoss;
                boolean gapDown = nextQuote.getOpen() < stopLoss;

                // closed on SL
                if (slInsideBar || slAtLow || gapDown) {
                    if (gapDown) {
                        stopLoss = nextQuote.getOpen();
                    }
                    double closingPositionSize = lots * stopLoss;
                    loss = closingPositionSize - openPositionSize;
                    closePositionQuote = nextQuote;
                    caughtGapDown = gapDown;
                    roi = NumbersJava.roi(openPositionSize, closingPositionSize);
                    closePositionPrice = stopLoss;
                    closePositionCost = closingPositionSize;
                    break;
                }

                // cannot move the SL down
                if (stopLossStrategy.isVolatile() && stopLossStrategy.calculate(data, startPositionIndex) > stopLoss) {
                    stopLoss = stopLossStrategy.calculate(data, startPositionIndex);
                }

                // cannot move TP down
                if (takeProfitStrategy.isVolatile() && takeProfitStrategy.calcualte(data, startPositionIndex) > takeProfit) {
                    takeProfit = takeProfitStrategy.calcualte(data, startPositionIndex);
                }
            }
            if (closePositionQuote != null) {
                positionTestResult.setOpenedTimestamp(signal.getTimestamp());
                positionTestResult.setClosedTimestamp(closePositionQuote.getTimestamp());
                positionTestResult.setClosed(true);
                positionTestResult.setProfitable(profitable);
                positionTestResult.setProfit(profit);
                positionTestResult.setCommissions(commissions);
                positionTestResult.setLoss(loss);
                positionTestResult.setGapUp(caughtGapUp);
                positionTestResult.setGapDown(caughtGapDown);
                positionTestResult.setRoi(NumbersJava.round(roi));
                positionTestResult.setOpenPositionPrice(openingPrice);
                positionTestResult.setOpenPositionCost(openPositionSize);
                positionTestResult.setClosePositionPrice(closePositionPrice);
                positionTestResult.setClosePositionCost(closePositionCost);
                testLog.append("\tclose price: " + NumbersJava.round(closePositionPrice) + lineSeparator());
                testLog.append("\tclosed: " + closePositionQuote.getNativeDate() + lineSeparator());
                testLog.append("\tprofitable: " + profitable + lineSeparator());
                testLog.append("\tgap up: " + caughtGapUp + lineSeparator());
                testLog.append("\tgap down: " + caughtGapDown + lineSeparator());
            }
            historicalTesting.addTestResult(signal, positionTestResult);
            testLog.append(lineSeparator() + lineSeparator());
        }

    }

    private static void rewind(SymbolDataJava screen, int i) {
        screen.quotes = screen.quotes.subList(0, screen.quotes.size() - i);
        Map<IndicatorJava, List<? extends AbstractModelJava>> indicators = new HashMap<>();
        screen.indicators.forEach((indicator, data) -> {
            indicators.put(indicator, data.subList(0, data.size() - i));
        });
        screen.indicators = indicators;
    }

    private static boolean isDataFilled(SymbolDataJava screen1, SymbolDataJava screen2) {
        AtomicBoolean filledData = new AtomicBoolean(true);

        int screenOneMinBarCount = resolveMinBarsCount(screen1.timeframe);
        int screenTwoMinBarCount = resolveMinBarsCount(screen2.timeframe);

        if (screen1.quotes.size() < screenOneMinBarCount || screen2.quotes.size() < screenTwoMinBarCount) {
            filledData.set(false);
        }
        screen1.indicators.forEach(((indicator, data) -> {
            if (data.size() < screenOneMinBarCount) {
                filledData.set(false);
                return;
            }
        }));
        screen2.indicators.forEach(((indicator, data) -> {
            if (data.size() < screenTwoMinBarCount) {
                filledData.set(false);
                return;
            }
        }));
        return filledData.get();
    }

    private static boolean hasHistory(SymbolDataJava screen1, SymbolDataJava screen2) {
        AtomicBoolean hasHistory = new AtomicBoolean(true);
        if (screen1.quotes.isEmpty() || screen2.quotes.isEmpty()) {
            hasHistory.set(false);
        }
        screen1.indicators.forEach(((indicator, data) -> {
            if (data.isEmpty()) {
                hasHistory.set(false);
                return;
            }
        }));
        screen2.indicators.forEach(((indicator, data) -> {
            if (data.isEmpty()) {
                hasHistory.set(false);
                return;
            }
        }));
        return hasHistory.get();
    }

}
