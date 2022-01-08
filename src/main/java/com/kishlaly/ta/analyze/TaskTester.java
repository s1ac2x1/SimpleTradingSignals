package com.kishlaly.ta.analyze;

import com.kishlaly.ta.model.HistoricalTesting;
import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.Timeframe;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.model.indicators.Keltner;
import com.kishlaly.ta.utils.Context;
import com.kishlaly.ta.utils.IndicatorUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.kishlaly.ta.cache.CacheReader.getSymbolData;
import static com.kishlaly.ta.cache.CacheReader.getSymbols;
import static com.kishlaly.ta.model.HistoricalTesting.Result;
import static com.kishlaly.ta.utils.Clean.clear;

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
                        calculateStatistics(testing);
                        historicalTestings.add(testing);
                        String key = "[" + screens[0].name() + "][" + screens[1] + "] " + task.name() + " - " + symbol;
                        Set<String> signalResults = readableOutput.get(key);
                        if (signalResults == null) {
                            signalResults = new LinkedHashSet<>();
                        }
                        Set<String> finalSignalResults = signalResults;
                        signals.forEach(signal -> {
                            String date = signal.getMyDate();
                            ZonedDateTime parsedDate = ZonedDateTime.parse(date);
                            date = parsedDate.getDayOfMonth() + " " + parsedDate.getMonth() + " " + parsedDate.getYear();
                            if (screen2.timeframe == Timeframe.HOUR) {
                                date += " " + parsedDate.getHour() + ":" + parsedDate.getMinute();
                            }
                            Result result = testing.getResult(signal);
                            String line = date + " --- " + result.isProfitable();
                            finalSignalResults.add(line);
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

    /**
     * Простое тестирование длинных позиций
     * TP на верхней границе канала Кельтнера
     * SL выбирается на 27 центов ниже самого низкого quote.low из десяти столбиков перед сигнальной котировкой (TODO реализовать)
     *
     * TODO адаптировать для коротких позиций тоже
     *
     * @param historicalTesting
     */
    private static void calculateStatistics(HistoricalTesting historicalTesting) {
        SymbolData data = historicalTesting.getData();
        List<Quote> quotes = data.quotes;
        data.indicators.put(Indicator.KELTNER, IndicatorUtils.buildKeltnerChannels(quotes));
        List<Quote> signals = historicalTesting.getSignals();
        // TODO remove
        signals = signals.subList(1, 2);
        // ---
        signals.forEach(signal -> {
            // найти индекс этой котировки в списке
            int signalIndex = -1;
            for (int i = 0; i < quotes.size(); i++) {
                if (quotes.get(i).getTimestamp().compareTo(signal.getTimestamp()) == 0) {
                    signalIndex = i;
                    break;
                }
            }
            if (signalIndex - 1 >= 2) {
                Keltner keltner = (Keltner) data.indicators.get(Indicator.KELTNER).get(signalIndex);
                double openingPrice = signal.getClose() + 0.07;
                double openPositionSize = Context.lots * openingPrice;
                double takeProfit = keltner.getTop();
                Quote currentQuote = signal;
                Quote prevQuote = quotes.get(signalIndex - 1);
                int index = signalIndex;
                do {
                    if (prevQuote.getLow() > currentQuote.getLow()) {
                        break;
                    } else {
                        index--;
                        currentQuote = quotes.get(index);
                        prevQuote = quotes.get(index - 1);
                    }
                } while (index > 2);
                double stopLoss = currentQuote.getLow() - 0.27;
                int startPositionIndex = signalIndex;
                double profit = 0;
                double loss = 0;
                boolean profitable = false;
                Quote closePositionQuote = null;
                while (startPositionIndex < quotes.size()) {
                    startPositionIndex++;
                    Quote nextQuote = quotes.get(startPositionIndex);
                    if (takeProfit > nextQuote.getLow() && takeProfit < nextQuote.getHigh()) {
                        // закрылся по TP
                        double closingPositionSize = Context.lots * takeProfit;
                        profit = closingPositionSize - openPositionSize;
                        profitable = true;
                        closePositionQuote = nextQuote;
                        break;
                    }
                    if (stopLoss > nextQuote.getLow() && stopLoss < nextQuote.getHigh()) {
                        // закрылся по SL
                        double closingPositionSize = Context.lots * stopLoss;
                        loss = openPositionSize - closingPositionSize;
                        closePositionQuote = nextQuote;
                        break;
                    }
                    // TODO собрать разную статистику:
                    // колчество сигналов по символу, сколько закрылось по TP / SL в числах и процентах
                    // среднее расстояние между входом и выходом из сделки
                    // напечатать все вместе
                }
                Result result = new Result();
                if (closePositionQuote != null) {
                    result.setClosed(true);
                    result.setProfitable(profitable);
                    result.setProfit(profit);
                    result.setLoss(loss);
                    if (profit > 0 || loss > 0) {
                        result.setTimeDiff(closePositionQuote.getTimestamp() - signal.getTimestamp());
                    }
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
