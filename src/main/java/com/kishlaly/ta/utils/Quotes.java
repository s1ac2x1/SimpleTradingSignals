package com.kishlaly.ta.utils;

import com.kishlaly.ta.model.QuoteJava;
import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.model.TimeframeJava;
import com.kishlaly.ta.model.indicators.BollingerJava;
import com.kishlaly.ta.model.indicators.KeltnerJava;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Quotes {

    public static List<QuoteJava> dayToWeek(List<QuoteJava> dailyQuotes) {
        List<QuoteJava> weeklyQuotes = new ArrayList<>();
        Set<QuoteJava> week = new HashSet<>();
        List<QuoteJava> weekSorted;
        for (int i = 0; i < dailyQuotes.size() - 1; i++) {
            try {
                QuoteJava currentQuote = dailyQuotes.get(i);
                QuoteJava nextQuote = dailyQuotes.get(i + 1);
                week.add(currentQuote);
                // check the day of the week for the next quote and programmatically calculate the next day for the current one
                ZonedDateTime currentQuoteDate = DatesJava.getTimeInExchangeZone(currentQuote.getTimestamp(), QuoteJava.exchangeTimezome);
                ZonedDateTime currentQuotePlusOneDayDate = DatesJava.getTimeInExchangeZone(currentQuote.getTimestamp(), QuoteJava.exchangeTimezome).plusDays(1);
                ZonedDateTime nextQuoteDate = DatesJava.getTimeInExchangeZone(nextQuote.getTimestamp(), QuoteJava.exchangeTimezome);
                // If the next quote is exactly one day later - we are within the working week
                // if currentQuotePlusOneDayDate is a day off, then nextQuoteDate will be different
                if (nextQuoteDate.getDayOfWeek() == currentQuotePlusOneDayDate.getDayOfWeek()) {
                    // we exclude the situation like that:
                    // week 1: MON TUE WED THU -
                    // week 2: -    -   -   -  FRI
                    // There will always be a difference of more than five days
                    long daysBetweenCurrentAndNextQuote = ChronoUnit.DAYS.between(currentQuoteDate, nextQuoteDate);
                    if (daysBetweenCurrentAndNextQuote < 5) {
                        week.add(currentQuote);
                        week.add(nextQuote);
                    }
                } else {
                    weekSorted = new ArrayList<>(week);
                    Collections.sort(weekSorted, Comparator.comparing(QuoteJava::getTimestamp));
                    long timestamp = weekSorted.get(0).getTimestamp();
                    double high = weekSorted.stream().mapToDouble(q -> q.getHigh()).max().getAsDouble();
                    double open = weekSorted.get(0).getOpen();
                    double close = weekSorted.get(week.size() - 1).getClose();
                    double low = weekSorted.stream().mapToDouble(q -> q.getLow()).min().getAsDouble();
                    double volume = weekSorted.stream().mapToDouble(q -> q.getVolume()).sum();
                    QuoteJava weeklyQuote = new QuoteJava(timestamp, NumbersJava.round(high), open, close, NumbersJava.round(low), volume);
                    weeklyQuotes.add(weeklyQuote);
                    week.clear();
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        Collections.sort(weeklyQuotes, Comparator.comparing(QuoteJava::getTimestamp));
        return weeklyQuotes;
    }

    public static List<QuoteJava> hourToDay(List<QuoteJava> hourQuotes) {
        List<QuoteJava> dayQuotes = new ArrayList<>();
        List<QuoteJava> duringDay = new ArrayList<>();
        for (int i = 0; i < hourQuotes.size() - 1; i++) {
            QuoteJava currentQuote = hourQuotes.get(i);
            DayOfWeek currentQuoteDayOfWeek = DatesJava.getTimeInExchangeZone(currentQuote.getTimestamp(), QuoteJava.exchangeTimezome).getDayOfWeek();
            QuoteJava nextQuote = hourQuotes.get(i + 1);
            DayOfWeek nextQuoteDayOfWeek = DatesJava.getTimeInExchangeZone(nextQuote.getTimestamp(), QuoteJava.exchangeTimezome).getDayOfWeek();
            if (currentQuoteDayOfWeek == nextQuoteDayOfWeek) {
                duringDay.add(nextQuote);
            } else {
                collectDayQuote(duringDay, dayQuotes);
                duringDay.add(nextQuote);
            }
        }
        collectDayQuote(duringDay, dayQuotes);
        return dayQuotes;
    }

    public static int resolveMinBarsCount(TimeframeJava timeframe) {
        int min = 21;
        // less is not allowed, otherwise StopLossFixedPrice will not work
        // If the aggregation is based on hourly quotes (which are ~550), then the daily quotes will be ~35, and there is no point in considering weekly ones
        // If the aggregation on the basis of daily quotes (which are up to 5500), then the weekly will be up to 1110
        return min;
    }

    public static void trim(SymbolDataJava screen) {
        if (screen.quotes != null && !screen.quotes.isEmpty()) {
            screen.quotes = screen.quotes.subList(screen.quotes.size() - resolveMinBarsCount(screen.timeframe), screen.quotes.size());
        } else {
            screen.quotes = new ArrayList<>();
        }
    }

    private static void collectDayQuote(List<QuoteJava> hourQuotesInsideOneDay, List<QuoteJava> dayQuotes) {
        List<QuoteJava> dayQuotesSorted = new ArrayList<>(hourQuotesInsideOneDay);
        Collections.sort(dayQuotesSorted, Comparator.comparing(QuoteJava::getTimestamp));
        if (dayQuotesSorted.isEmpty()) {
            System.out.println("Warning: hourQuotesInsideOneDay is empty");
            return;
        }
        long timestamp = dayQuotesSorted.get(0).getTimestamp();
        double high = dayQuotesSorted.stream().mapToDouble(q -> q.getHigh()).max().getAsDouble();
        double open = dayQuotesSorted.get(0).getOpen();
        double close = dayQuotesSorted.get(dayQuotesSorted.size() - 1).getClose();
        double low = dayQuotesSorted.stream().mapToDouble(q -> q.getLow()).min().getAsDouble();
        double volume = dayQuotesSorted.stream().mapToDouble(q -> q.getVolume()).sum();
        QuoteJava dayQuote = new QuoteJava(timestamp, NumbersJava.round(high), open, close, NumbersJava.round(low), volume);
        dayQuotes.add(dayQuote);
        hourQuotesInsideOneDay.clear();
    }

    public static boolean isQuoteCrossedEMA(QuoteJava quote, double emaValue) {
        return quote.getLow() <= emaValue && quote.getHigh() >= emaValue;
    }

    public static boolean isQuoteBelowEMA(QuoteJava quote, double emaValue) {
        return quote.getLow() < emaValue && quote.getHigh() < emaValue;
    }

    public static boolean isQuoteAboveEMA(QuoteJava quote, double emaValue) {
        return quote.getLow() > emaValue && quote.getHigh() > emaValue;
    }

    public static boolean isQuoteCrossedBollingerBottom(QuoteJava quote, BollingerJava bollinger) {
        return quote.getLow() <= bollinger.getBottom() && quote.getHigh() >= bollinger.getBottom();
    }

    public static boolean isQuoteBelowBollingerBottom(QuoteJava quote, BollingerJava bollinger) {
        return quote.getLow() < bollinger.getBottom() && quote.getHigh() < bollinger.getBottom();
    }

    public static boolean isQuoteCrossedKeltnerBottom(QuoteJava quote, KeltnerJava keltner) {
        return quote.getLow() <= keltner.getLow() && quote.getHigh() >= keltner.getLow();
    }

    public static boolean isGreen(QuoteJava quote) {
        return quote.getClose() > quote.getOpen();
    }


}
