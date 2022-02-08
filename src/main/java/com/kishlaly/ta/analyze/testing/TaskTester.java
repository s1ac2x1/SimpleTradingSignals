package com.kishlaly.ta.analyze.testing;

import com.kishlaly.ta.analyze.TaskType;
import com.kishlaly.ta.analyze.testing.sl.StopLossStrategy;
import com.kishlaly.ta.analyze.testing.tp.TakeProfitStrategy;
import com.kishlaly.ta.model.*;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.utils.Context;
import com.kishlaly.ta.utils.Numbers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static com.kishlaly.ta.cache.CacheReader.getSymbolData;
import static com.kishlaly.ta.cache.CacheReader.getSymbols;
import static com.kishlaly.ta.model.HistoricalTesting.PositionTestResult;
import static com.kishlaly.ta.model.Quote.exchangeTimezome;
import static com.kishlaly.ta.utils.Clean.clear;
import static com.kishlaly.ta.utils.Dates.getBarTimeInMyZone;
import static com.kishlaly.ta.utils.Quotes.resolveMinBarCount;

public class TaskTester {

    public static void test(Timeframe[][] timeframes, TaskType[] tasks) {
        Context.testMode = true;
        Set<String> symbols = getSymbols();
        StringBuilder log = new StringBuilder();
        List<HistoricalTesting> allTests = new ArrayList<>();
        Arrays.stream(timeframes).forEach(screens -> {
            Arrays.stream(tasks).forEach(task -> {
                task.updateTimeframeForScreen(1, screens[0]);
                task.updateTimeframeForScreen(2, screens[1]);
                Map<String, Set<String>> readableOutput = new HashMap<>();
                symbols.forEach(symbol -> {
                    SymbolData screen1 = getSymbolData(task.getTimeframeIndicators(1), symbol);
                    SymbolData screen2 = getSymbolData(task.getTimeframeIndicators(2), symbol);
                    SymbolData symbolDataForTesting = getSymbolData(task.getTimeframeIndicators(2), symbol);
                    List<TaskResult> taskResults = new ArrayList<>();
                    if (!isDataFilled(screen1, screen2)) {
                        return;
                    }
                    try {
                        while (hasHistory(screen1, screen2)) {
                            Quote lastScreen1Quote = screen1.quotes.get(screen1.quotes.size() - 1);
                            Quote lastScreen2Quote = screen2.quotes.get(screen2.quotes.size() - 1);
                            taskResults.add(task.getFunction().apply(screen1, screen2));
                            if (lastScreen2Quote.getTimestamp() < lastScreen1Quote.getTimestamp()) {
                                rewind(screen1, 1);
                            } else {
                                rewind(screen2, 1);
                            }
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    if (!taskResults.isEmpty()) {
                        HistoricalTesting testing = null;
                        if (Context.massTesting) {
                            if (Context.takeProfitStrategies != null) {
                                Context.takeProfitStrategies.forEach(takeProfitStrategy -> {
                                    HistoricalTesting massTesting = new HistoricalTesting(symbolDataForTesting, taskResults, Context.stopLossStrategy, takeProfitStrategy);
                                    calculateStatistics(massTesting);
                                    allTests.add(massTesting);
                                });
                            }
                        } else {
                            testing = new HistoricalTesting(symbolDataForTesting, taskResults, Context.stopLossStrategy, Context.takeProfitStrategy);
                            calculateStatistics(testing);
                            allTests.add(testing);
                            String key = "[" + screens[0].name() + "][" + screens[1] + "] " + task.name() + " - " + symbol;
                            Set<String> signalResults = readableOutput.get(key);
                            if (signalResults == null) {
                                signalResults = new LinkedHashSet<>();
                            }
                            signalResults.add(formatTestingSummary(testing));
                            Set<String> finalSignalResults = signalResults;

                            // на данном этапе HistoricalTesting содержит тесты позиций по сигналам
                            // а так же все результаты отказов
                            // TaskResult.lastChartQuote может быть null, если стратегии не хватило котировок для теста

                            // сначала печатаем отчет по позициям
                            HistoricalTesting finalTesting = testing;
                            testing.getTaskResults()
                                    .stream()
                                    .filter(taskResult -> taskResult.getLastChartQuote() != null)
                                    .filter(taskResult -> taskResult.isSignal())
                                    .forEach(taskResult -> {
                                        String line = "";
                                        Quote quote = taskResult.getLastChartQuote();
                                        String quoteDateFormatted = formatDate(screen2.timeframe, quote.getTimestamp());

                                        // сначала печатаем результаты тестирования сигналов
                                        if (taskResult.isSignal()) {
                                            PositionTestResult positionTestResult = finalTesting.getResult(quote);
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
                                                line += " " + positionTestResult.getPositionDuration(screen2.timeframe);
                                                String endDate = getBarTimeInMyZone(positionTestResult.getClosedTimestamp(), exchangeTimezome).toString();
                                                ZonedDateTime parsed = ZonedDateTime.parse(endDate);
                                                String parsedEndDate = parsed.getDayOfMonth() + " " + parsed.getMonth() + " " + parsed.getYear();
                                                if (screen2.timeframe == Timeframe.HOUR) {
                                                    parsedEndDate += " " + parsed.getHour() + ":" + parsed.getMinute();
                                                }
                                                line += " [till " + parsedEndDate + "]";
                                            }
                                            finalSignalResults.add(quoteDateFormatted + " --- " + line);
                                        }
                                    });

                            // потом лог всех остальных котировок с указанием причины, почему стратегия дала отказ
                            finalSignalResults.add(System.lineSeparator());
                            finalSignalResults.add(System.lineSeparator());
                            finalSignalResults.add(System.lineSeparator());
                            testing.getTaskResults()
                                    .stream()
                                    .filter(taskResult -> taskResult.getLastChartQuote() != null)
                                    .filter(taskResult -> !taskResult.isSignal())
                                    .forEach(taskResult -> {
                                        String quoteDateFormatted = formatDate(screen2.timeframe, taskResult.getLastChartQuote().getTimestamp());
                                        finalSignalResults.add(quoteDateFormatted + " ### " + taskResult.getCode());
                                    });
                            readableOutput.put(key, signalResults);
                        }
                    }
                    clear(screen1);
                    clear(screen2);
                });
                if (!Context.massTesting) {
                    readableOutput.forEach((key, data) -> {
                        log.append(key).append("\t").append(System.lineSeparator());
                        data.forEach(line -> log.append("    " + line).append(System.lineSeparator()));
                        log.append(System.lineSeparator());
                    });
                }
            });
        });
        if (!Context.massTesting) {
            try {
                Files.write(Paths.get("tests.txt"), log.toString().getBytes());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } else {
            StringBuilder builder = new StringBuilder();
            allTests.forEach(testing -> {
                if (Context.takeProfitStrategies != null) {
                    builder.append("TP: " + testing.printTP() + " => TP/SL = " + testing.printTPSLNumber() + " (" + testing.printTPSLPercent() + "); balance = " + testing.getBalance()).append(System.lineSeparator());
                }
            });
            try {
                Files.write(Paths.get("mass_tests.txt"), builder.toString().getBytes());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static String formatDate(Timeframe timeframe, long timestamp) {
        String date = getBarTimeInMyZone(timestamp, exchangeTimezome).toString();
        ZonedDateTime parsedDate = ZonedDateTime.parse(date);
        date = parsedDate.getDayOfMonth() + " " + parsedDate.getMonth() + " " + parsedDate.getYear();
        if (timeframe == Timeframe.HOUR) {
            date += " " + parsedDate.getHour() + ":" + parsedDate.getMinute();
        }
        return date;
    }

    private static String formatTestingSummary(HistoricalTesting testing) {
        String result = "";
        result += "trendCheckIncludeHistogram = " + Context.trendCheckIncludeHistogram + System.lineSeparator();
        result += "\tSL: " + testing.printSL() + System.lineSeparator();
        result += "\tTP: " + testing.printTP() + System.lineSeparator();
        result += "\tTP/SL = " + testing.printTPSLNumber() + " = ";
        result += testing.printTPSLPercent() + "%" + System.lineSeparator();
        double balance = testing.getBalance();
        balance = balance - balance / 100 * 10;
        result += "\tTotal balance (minus 10% commissions) = " + Numbers.round(balance) + System.lineSeparator();
        result += "\tTotal profit / loss = " + testing.getTotalProfit() + " / " + testing.getTotalLoss() + System.lineSeparator();
        long minPositionDurationSeconds = testing.getMinPositionDurationSeconds();
        long maxPositionDurationSeconds = testing.getMaxPositionDurationSeconds();
        String longestPositionRange = formatRange(testing, t -> t.searchSignalByLongestPosition());
        long averagePositionDurationSeconds = testing.getAveragePositionDurationSeconds();
        switch (testing.getData().timeframe) {
            case DAY:
                int minPositionDurationDays = (int) TimeUnit.SECONDS.toDays(minPositionDurationSeconds);
                int maxPositionDurationDays = (int) TimeUnit.SECONDS.toDays(maxPositionDurationSeconds);
                int avgPositionDurationDays = (int) TimeUnit.SECONDS.toDays(averagePositionDurationSeconds);
                result += "\tmin duration = " + minPositionDurationDays + " days" + System.lineSeparator();
                result += "\tmax duration = " + maxPositionDurationDays + " days " + longestPositionRange + System.lineSeparator();
                result += "\tavg duration = " + avgPositionDurationDays + " days" + System.lineSeparator();
                break;
            case HOUR:
                int minPositionDurationHours = (int) TimeUnit.SECONDS.toHours(minPositionDurationSeconds);
                int maxPositionDurationHours = (int) TimeUnit.SECONDS.toHours(maxPositionDurationSeconds);
                int avgPositionDurationHours = (int) TimeUnit.SECONDS.toHours(averagePositionDurationSeconds);
                result += "\tmin duration = " + minPositionDurationHours + " hours" + System.lineSeparator();
                result += "\tmax duration = " + maxPositionDurationHours + " hours" + System.lineSeparator(); // TODO сюда диапазон
                result += "\tavg duration = " + avgPositionDurationHours + " hours" + System.lineSeparator();
                break;
        }
        if (testing.searchSignalByProfit(testing.getMinProfit()) != null) {
            result += "\tmin profit = " + testing.searchSignalByProfit(testing.getMinProfit()).getRoi() + "% " + formatRange(testing, t -> t.searchSignalByProfit(t.getMinProfit())) + System.lineSeparator();
        }
        if (testing.searchSignalByProfit(testing.getMaxProfit()) != null) {
            result += "\tmax profit = " + testing.searchSignalByProfit(testing.getMaxProfit()).getRoi() + "% " + formatRange(testing, t -> t.searchSignalByProfit(t.getMaxProfit())) + System.lineSeparator();
        }
        if (testing.searchSignalByLoss(testing.getMinLoss()) != null) {
            result += "\tmin loss = " + testing.searchSignalByLoss(testing.getMinLoss()).getRoi() + "% " + formatRange(testing, t -> t.searchSignalByLoss(t.getMinLoss())) + System.lineSeparator();
        }
        if (testing.searchSignalByLoss(testing.getMaxLoss()) != null) {
            result += "\tmax loss = " + testing.searchSignalByLoss(testing.getMaxLoss()).getRoi() + "% " + formatRange(testing, t -> t.searchSignalByLoss(t.getMaxLoss())) + System.lineSeparator();
        }
        result += "\tavg profit / loss = " + testing.getAvgProfit() + " / " + testing.getAvgLoss() + System.lineSeparator();
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

    /**
     * Простое тестирование длинных позиций
     * <p>
     * TODO адаптировать для коротких позиций тоже
     *
     * @param historicalTesting
     */
    private static void calculateStatistics(HistoricalTesting historicalTesting) {
        historicalTesting.getTaskResults()
                .stream()
                .filter(taskResult -> taskResult.isSignal()) // берем только сигналы к входу
                .forEach(taskResult -> testPosition(taskResult, historicalTesting));
    }

    private static void testPosition(TaskResult taskResult, HistoricalTesting historicalTesting) {
        PositionTestResult positionTestResult = new PositionTestResult();
        Quote signal = taskResult.getLastChartQuote();
        int signalIndex = -1;
        SymbolData data = historicalTesting.getData();
        for (int i = 0; i < data.quotes.size(); i++) {
            if (data.quotes.get(i).getTimestamp().compareTo(signal.getTimestamp()) == 0) {
                signalIndex = i;
                break;
            }
        }
        if (signalIndex > 11) {
            StopLossStrategy stopLossStrategy = historicalTesting.getStopLossStrategy();
            double stopLoss = stopLossStrategy.calculate(data, signalIndex);

            TakeProfitStrategy takeProfitStrategy = historicalTesting.getTakeProfitStrategy();
            double takeProfit = takeProfitStrategy.calcualte(data, signalIndex);

            double openingPrice = signal.getClose() + 0.07;
            double openPositionSize = Context.lots * openingPrice;
            int startPositionIndex = signalIndex;
            double profit = 0;
            double loss = 0;
            boolean profitable = false;
            boolean caughtGapUp = false;
            boolean caughtGapDown = false;
            double roi = 0;
            double closePositionPrice = 0;
            double closePositionCost = 0;
            Quote closePositionQuote = null;
            while (startPositionIndex < data.quotes.size() - 1) {
                startPositionIndex++;
                Quote nextQuote = data.quotes.get(startPositionIndex);
                boolean tpInsideBar = takeProfitStrategy.isEnabled()
                        ? nextQuote.getLow() < takeProfit && nextQuote.getHigh() > takeProfit
                        : false;
                boolean tpAtHigh = takeProfitStrategy.isEnabled()
                        ? nextQuote.getHigh() == takeProfit
                        : false;
                boolean gapUp = takeProfitStrategy.isEnabled()
                        ? nextQuote.getOpen() > takeProfit
                        : false;
                // закрылся по TP
                if (tpInsideBar || tpAtHigh || gapUp) {
                    if (gapUp) {
                        takeProfit = nextQuote.getOpen();
                    }
                    double closingPositionSize = Context.lots * takeProfit;
                    profit = closingPositionSize - openPositionSize;
                    roi = Numbers.roi(openPositionSize, closingPositionSize);
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
                // закрылся по SL
                if (slInsideBar || slAtLow || gapDown) {
                    if (gapDown) {
                        stopLoss = nextQuote.getOpen();
                    }
                    double closingPositionSize = Context.lots * stopLoss;
                    loss = closingPositionSize - openPositionSize;
                    closePositionQuote = nextQuote;
                    caughtGapDown = gapDown;
                    roi = Numbers.roi(openPositionSize, closingPositionSize);
                    closePositionPrice = stopLoss;
                    closePositionCost = closingPositionSize;
                    break;
                }
                if (stopLossStrategy.isVolatile() && stopLossStrategy.calculate(data, startPositionIndex) > stopLoss) {
                    stopLoss = stopLossStrategy.calculate(data, startPositionIndex);
                }
            }
            if (closePositionQuote != null) {
                positionTestResult.setOpenedTimestamp(signal.getTimestamp());
                positionTestResult.setClosedTimestamp(closePositionQuote.getTimestamp());
                positionTestResult.setClosed(true);
                positionTestResult.setProfitable(profitable);
                positionTestResult.setProfit(profit);
                positionTestResult.setLoss(loss);
                positionTestResult.setGapUp(caughtGapUp);
                positionTestResult.setGapDown(caughtGapDown);
                positionTestResult.setRoi(Numbers.round(roi));
                positionTestResult.setOpenPositionPrice(openingPrice);
                positionTestResult.setOpenPositionCost(openPositionSize);
                positionTestResult.setClosePositionPrice(closePositionPrice);
                positionTestResult.setClosePositionCost(closePositionCost);
            }
            historicalTesting.addTestResult(signal, positionTestResult);
        }

    }

    private static void rewind(SymbolData screen, int i) {
        screen.quotes = screen.quotes.subList(0, screen.quotes.size() - i);
        Map<Indicator, List> indicators = new HashMap<>();
        screen.indicators.forEach((indicator, data) -> {
            indicators.put(indicator, data.subList(0, data.size() - i));
        });
        screen.indicators = indicators;
    }

    private static boolean isDataFilled(SymbolData screen1, SymbolData screen2) {
        AtomicBoolean filledData = new AtomicBoolean(true);

        int screenOneMinBarCount = resolveMinBarCount(screen1.timeframe);
        int screenTwoMinBarCount = resolveMinBarCount(screen2.timeframe);

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

    private static boolean hasHistory(SymbolData screen1, SymbolData screen2) {
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
