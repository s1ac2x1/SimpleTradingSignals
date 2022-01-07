package com.kishlaly.ta.analyze;

import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.Timeframe;
import com.kishlaly.ta.model.indicators.Indicator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.kishlaly.ta.cache.CacheReader.getSymbolData;
import static com.kishlaly.ta.cache.CacheReader.getSymbols;
import static com.kishlaly.ta.utils.Clean.clear;

public class TaskTester {

    public static void test(Timeframe[][] timeframes, TaskType[] tasks) {
        Set<String> symbols = getSymbols();
        StringBuilder log = new StringBuilder();
        Arrays.stream(timeframes).forEach(screens -> {
            Arrays.stream(tasks).forEach(task -> {
                task.updateTimeframeForScreen(1, screens[0]);
                task.updateTimeframeForScreen(2, screens[1]);
                Map<String, Set<String>> output = new HashMap<>();
                symbols.forEach(symbol -> {
                    SymbolData screen1 = getSymbolData(task.getTimeframeIndicators(1), symbol);
                    SymbolData screen2 = getSymbolData(task.getTimeframeIndicators(2), symbol);
                    List<Quote> signals = new ArrayList<>();
                    if (!isDataFilled(screen1, screen2)) {
                        return;
                    }
                    try {
                        // signal -> коллекция
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
                        String key = "[" + screens[0].name() + "][" + screens[1] + "] " + task.name() + " - " + symbol;
                        Set<String> signalDates = output.get(key);
                        if (signalDates == null) {
                            signalDates = new LinkedHashSet<>();
                        }
                        Set<String> finalSignalDates = signalDates;
                        signals.forEach(signal -> {
                            String date = signal.getMyDate();
                            ZonedDateTime parsedDate = ZonedDateTime.parse(date);
                            date = parsedDate.getDayOfMonth() + " " + parsedDate.getMonth() + " " + parsedDate.getYear();
                            if (screen2.timeframe == Timeframe.HOUR) {
                                date += " " + parsedDate.getHour() + ":" + parsedDate.getMinute();
                            }
                            finalSignalDates.add(date);
                        });
                        output.put(key, signalDates);
                    }
                    clear(screen1);
                    clear(screen2);
                });
                output.forEach((key, data) -> {
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
