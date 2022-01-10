package com.kishlaly.ta.analyze;

import com.kishlaly.ta.model.HistoricalTesting;
import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.Timeframe;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.model.indicators.Keltner;
import com.kishlaly.ta.utils.Context;
import com.kishlaly.ta.utils.IndicatorUtils;
import com.kishlaly.ta.utils.Numbers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.kishlaly.ta.cache.CacheReader.getSymbolData;
import static com.kishlaly.ta.cache.CacheReader.getSymbols;
import static com.kishlaly.ta.model.HistoricalTesting.Result;
import static com.kishlaly.ta.model.Quote.exchangeTimezome;
import static com.kishlaly.ta.utils.Clean.clear;
import static com.kishlaly.ta.utils.Dates.getBarTimeInMyZone;

public class TaskTester {

    public static void test(Timeframe[][] timeframes, TaskType[] tasks) {
        Set<String> symbols = getSymbols();
        StringBuilder log = new StringBuilder();
        List<HistoricalTesting> historicalTestings = new ArrayList<>();
        Arrays.stream(timeframes).forEach(screens -> {
            Arrays.stream(tasks).forEach(task -> {
                task.updateTimeframeForScreen(1, screens[0]);
                task.updateTimeframeForScreen(2, screens[1]);
                Map<String, Set<String>> readableOutput = new HashMap<>();
                symbols.forEach(symbol -> {
                    SymbolData screen1 = getSymbolData(task.getTimeframeIndicators(1), symbol);
                    SymbolData screen2 = getSymbolData(task.getTimeframeIndicators(2), symbol);
                    SymbolData forTesting = getSymbolData(task.getTimeframeIndicators(2), symbol);
                    List<Quote> signals = new ArrayList<>();
                    if (!isDataFilled(screen1, screen2)) {
                        return;
                    }
                    try {
                        while (hasHistory(screen1, screen2)) {
                            Quote lastScreen1Quote = screen1.quotes.get(screen1.quotes.size() - 1);
                            Quote lastScreen2Quote = screen2.quotes.get(screen2.quotes.size() - 1);
                            Quote result = task.getFunction().apply(screen1, screen2);
                            if (result != null) {
                                signals.add(result);
                            }
                            if (lastScreen2Quote.getTimestamp() <= lastScreen1Quote.getTimestamp()) {
                                rewind(screen1, 1);
                            } else {
                                rewind(screen2, 1);
                            }
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    if (!signals.isEmpty()) {
                        HistoricalTesting testing = new HistoricalTesting(forTesting, signals);
                        try {
                            calculateStatistics(testing);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                        historicalTestings.add(testing);
                        String key = "[" + screens[0].name() + "][" + screens[1] + "] " + task.name() + " - " + symbol;
                        Set<String> signalResults = readableOutput.get(key);
                        if (signalResults == null) {
                            signalResults = new LinkedHashSet<>();
                        }
                        signalResults.add(formatTestingSummary(testing));
                        Set<String> finalSignalResults = signalResults;
                        signals.forEach(signal -> {
                            String signalDate = signal.getMyDate();
                            ZonedDateTime parsedDate = ZonedDateTime.parse(signalDate);
                            signalDate = parsedDate.getDayOfMonth() + " " + parsedDate.getMonth() + " " + parsedDate.getYear();
                            if (screen2.timeframe == Timeframe.HOUR) {
                                signalDate += " " + parsedDate.getHour() + ":" + parsedDate.getMinute();
                            }
                            Result result = testing.getResult(signal);
                            String line = "";
                            if (!result.isClosed()) {
                                line += " NOT CLOSED";
                            } else {
                                line += result.isProfitable() ? "PROFIT" : "LOSS";
                                if (result.isGapUp()) {
                                    line += " gap up";
                                }
                                if (result.isGapDown()) {
                                    line += " gap down";
                                }
                                line += System.lineSeparator();
                                line += "\t\tDuration " + result.getPositionDuration(screen2.timeframe);
                                String endDate = getBarTimeInMyZone(result.getClosedTimestamp(), exchangeTimezome).toString();
                                ZonedDateTime parsed = ZonedDateTime.parse(endDate);
                                String parsedEndDate = parsed.getDayOfMonth() + " " + parsed.getMonth() + " " + parsed.getYear();
                                if (screen2.timeframe == Timeframe.HOUR) {
                                    parsedEndDate += " " + parsed.getHour() + ":" + parsed.getMinute();
                                }
                                line += " [till " + parsedEndDate + "]";
                                line += System.lineSeparator();
                                if (result.isProfitable()) {
                                    line += "\t\tprofit: " + result.getProfit(); // TODO в процентах ROI
                                } else {
                                    line += "\t\tloss: " + result.getLoss(); // TODO в процентах ROI
                                }
                            }
                            finalSignalResults.add(signalDate + " --- " + line);
                        });
                        readableOutput.put(key, signalResults);
                    }
                    clear(screen1);
                    clear(screen2);
                });
                readableOutput.forEach((key, data) -> {
                    log.append(key).append("\t").append(System.lineSeparator());
                    data.forEach(line -> log.append("    " + line).append(System.lineSeparator()));
                    log.append(System.lineSeparator());
                });
            });
        });
        try {
            Files.write(Paths.get("tests.txt"), log.toString().getBytes());
        } catch (IOException e) {
            System.out.println(e.getMessage());
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
        result += "TP/SL = " + testing.getProfitablePositions() + "/" + testing.getLossPositions() + System.lineSeparator();
        double balance = testing.getTotalProfit() - testing.getTotalLoss();
        balance = balance - balance / 100 * 10;
        result += "\tTotal balance (minus 10% commissions) = " + Numbers.round(balance) + System.lineSeparator();
        result += "\tTotal profit / loss = " + testing.getTotalProfit() + " / " + testing.getTotalLoss() + System.lineSeparator();
        long minPositionDurationSeconds = testing.getMinPositionDurationSeconds();
        long maxPositionDurationSeconds = testing.getMaxPositionDurationSeconds();
        // TODO move to method (with lambda receiver)
        Result resultWithLongestPosition = testing.searchSignalByPositionDuration(maxPositionDurationSeconds);
        String longestPositionRange = "";
        if (resultWithLongestPosition != null) {
            longestPositionRange = "[" + formatDate(testing.getData().timeframe, resultWithLongestPosition.getOpenedTimestamp()) + " - " + formatDate(testing.getData().timeframe, resultWithLongestPosition.getClosedTimestamp()) + "]";
        }
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
        // TODO move to method (with lambda receiver)
        Result resultWithMaxProfit = testing.searchSignalByMaxProfit();
        String maxProfitPositionRange = "";
        if (resultWithMaxProfit != null) {
            maxProfitPositionRange = " [" + formatDate(testing.getData().timeframe, resultWithMaxProfit.getOpenedTimestamp()) + " - " + formatDate(testing.getData().timeframe, resultWithMaxProfit.getClosedTimestamp()) + "]";
        }
        // TODO move to method (with lambda receiver)
        Result resultWithMaxLoss = testing.searchSignalByMaxLoss();
        String maxLossPositionRange = "";
        if (resultWithMaxLoss != null) {
            maxLossPositionRange = " [" + formatDate(testing.getData().timeframe, resultWithMaxLoss.getOpenedTimestamp()) + " - " + formatDate(testing.getData().timeframe, resultWithMaxLoss.getClosedTimestamp()) + "]";
        }
        result += "\tmax profit = " + testing.getMaxProfit() + maxProfitPositionRange + System.lineSeparator();
        result += "\tmax loss = " + testing.getMaxLoss() + maxLossPositionRange + System.lineSeparator();
        result += "\tavg profit / loss = " + testing.getAvgProfit() + " / " + testing.getAvgLoss() + System.lineSeparator();
        return result;
    }

    /**
     * Простое тестирование длинных позиций
     * TP на верхней границе канала Кельтнера
     * SL выбирается на 27 центов ниже самого низкого quote.low из десяти столбиков перед сигнальной котировкой
     * <p>
     * TODO адаптировать для коротких позиций тоже
     *
     * @param historicalTesting
     */
    private static void calculateStatistics(HistoricalTesting historicalTesting) {
        SymbolData data = historicalTesting.getData();
        List<Quote> quotes = data.quotes;
        data.indicators.put(Indicator.KELTNER, IndicatorUtils.buildKeltnerChannels(quotes));
        List<Quote> signals = historicalTesting.getSignals();
        signals.forEach(signal -> {
            // найти индекс этой котировки в списке
            int signalIndex = -1;
            for (int i = 0; i < quotes.size(); i++) {
                if (quotes.get(i).getTimestamp().compareTo(signal.getTimestamp()) == 0) {
                    signalIndex = i;
                    break;
                }
            }
            if (signalIndex > 11) {
                Keltner keltner = (Keltner) data.indicators.get(Indicator.KELTNER).get(signalIndex);
                double openingPrice = signal.getClose() + 0.07;
                double openPositionSize = Context.lots * openingPrice;
                double takeProfit = keltner.getTop();
                // SL выбирается на 27 центов ниже самого низкого quote.low из десяти столбиков перед сигнальной котировкой
                Quote quoteWithMinimalLow = quotes.subList(signalIndex - 10, signalIndex).stream().min(Comparator.comparingDouble(quote -> quote.getLow())).get();
                double stopLoss = quoteWithMinimalLow.getLow() - 0.27;
                int startPositionIndex = signalIndex;
                double profit = 0;
                double loss = 0;
                boolean profitable = false;
                boolean caughtGapUp = false;
                boolean caughtGapDown = false;
                Quote closePositionQuote = null;
                while (startPositionIndex < quotes.size()) {
                    startPositionIndex++;
                    Quote nextQuote = quotes.get(startPositionIndex);
                    boolean tpInsideBar = nextQuote.getLow() < takeProfit && nextQuote.getHigh() > takeProfit;
                    boolean tpAtHigh = nextQuote.getHigh() == takeProfit;
                    boolean gapUp = nextQuote.getOpen() > takeProfit;
                    // закрылся по TP
                    if (tpInsideBar || tpAtHigh || gapUp) {
                        if (gapUp) {
                            takeProfit = nextQuote.getOpen();
                        }
                        double closingPositionSize = Context.lots * takeProfit;
                        profit = closingPositionSize - openPositionSize;
                        profitable = true;
                        closePositionQuote = nextQuote;
                        caughtGapUp = gapUp;
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
                        loss = openPositionSize - closingPositionSize;
                        closePositionQuote = nextQuote;
                        caughtGapDown = gapDown;
                        break;
                    }
                    // TODO собрать разную статистику:
                    // колчество сигналов по символу, сколько закрылось по TP / SL в числах и процентах
                    // среднее расстояние между входом и выходом из сделки
                    // количество гэпов на профите и лоссе
                    // напечатать все вместе
                }
                Result result = new Result();
                if (closePositionQuote != null) {
                    result.setOpenedTimestamp(signal.getTimestamp());
                    result.setClosedTimestamp(closePositionQuote.getTimestamp());
                    result.setClosed(true);
                    result.setProfitable(profitable);
                    result.setProfit(profit);
                    result.setLoss(loss);
                    result.setGapUp(caughtGapUp);
                    result.setGapDown(caughtGapDown);
                }
                historicalTesting.addSignalResult(signal, result);
            }
        });
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
        if (screen1.quotes.size() < 100 || screen2.quotes.size() < 100) {
            filledData.set(false);
        }
        screen1.indicators.forEach(((indicator, data) -> {
            if (data.size() < 100) {
                filledData.set(false);
                return;
            }
        }));
        screen2.indicators.forEach(((indicator, data) -> {
            if (data.size() < 100) {
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
