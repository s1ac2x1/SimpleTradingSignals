package com.kishlaly.ta.utils;

import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.Timeframe;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Quotes {

    public static List<Quote> dayToWeek(List<Quote> dailyQuotes) {
        List<Quote> weeklyQuotes = new ArrayList<>();
        Set<Quote> week = new HashSet<>();
        List<Quote> weekSorted;
        for (int i = 0; i < dailyQuotes.size() - 1; i++) {
            try {
                Quote currentQuote = dailyQuotes.get(i);
                Quote nextQuote = dailyQuotes.get(i + 1);
                week.add(currentQuote);
                // сверять день недели у следующей котировки и программно вычислять следующий день для текущей
                ZonedDateTime currentQuoteDate = Dates.getTimeInExchangeZone(currentQuote.getTimestamp(), Quote.exchangeTimezome);
                ZonedDateTime currentQuotePlusOneDayDate = Dates.getTimeInExchangeZone(currentQuote.getTimestamp(), Quote.exchangeTimezome).plusDays(1);
                ZonedDateTime nextQuoteDate = Dates.getTimeInExchangeZone(nextQuote.getTimestamp(), Quote.exchangeTimezome);
                // если следующая котировка ровно на один день позже - мы рамках рабочей недели
                // если currentQuotePlusOneDayDate выходной день, то nextQuoteDate будет другой
                if (nextQuoteDate.getDayOfWeek() == currentQuotePlusOneDayDate.getDayOfWeek()) {
                    // исключаем ситуацию вида:
                    // неделя 1: пн вт ср чт _
                    // неделя 2: -  -  -  -  пт
                    // тут всегда будет разница в более чем пять дней
                    long daysBetweenCurrentAndNextQuote = ChronoUnit.DAYS.between(currentQuoteDate, nextQuoteDate);
                    if (daysBetweenCurrentAndNextQuote < 5) {
                        week.add(currentQuote);
                        week.add(nextQuote);
                    }
                } else {
                    weekSorted = new ArrayList<>(week);
                    Collections.sort(weekSorted, Comparator.comparing(Quote::getTimestamp));
                    long timestamp = weekSorted.get(0).getTimestamp();
                    double high = weekSorted.stream().mapToDouble(q -> q.getHigh()).max().getAsDouble();
                    double open = weekSorted.get(0).getOpen();
                    double close = weekSorted.get(week.size() - 1).getClose();
                    double low = weekSorted.stream().mapToDouble(q -> q.getLow()).min().getAsDouble();
                    double volume = weekSorted.stream().mapToDouble(q -> q.getVolume()).sum();
                    Quote weeklyQuote = new Quote(timestamp, Numbers.round(high), open, close, Numbers.round(low), volume);
                    weeklyQuotes.add(weeklyQuote);
                    week.clear();
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        Collections.sort(weeklyQuotes, Comparator.comparing(Quote::getTimestamp));
        return weeklyQuotes;
    }

    public static List<Quote> hourToDay(List<Quote> hourQuotes) {
        List<Quote> dayQuotes = new ArrayList<>();
        List<Quote> duringDay = new ArrayList<>();
        for (int i = 0; i < hourQuotes.size() - 1; i++) {
            Quote currentHour = hourQuotes.get(i);
            DayOfWeek currentHourDay = Dates.getTimeInExchangeZone(currentHour.getTimestamp(), Quote.exchangeTimezome).getDayOfWeek();
            Quote nextHour = hourQuotes.get(i + 1);
            DayOfWeek nextHourDay = Dates.getTimeInExchangeZone(nextHour.getTimestamp(), Quote.exchangeTimezome).getDayOfWeek();
            if (currentHourDay == nextHourDay) {
                duringDay.add(nextHour);
            } else {
                collectDayQuote(duringDay, dayQuotes);
                duringDay.add(nextHour);
            }
        }
        collectDayQuote(duringDay, dayQuotes);
        return dayQuotes;
    }

    public static int resolveMinBarCount(Timeframe timeframe) {
        switch (timeframe) {
            case WEEK:
                return 30;
            case DAY:
                return 250;
            case HOUR:
                return 300;
            default:
                return 100;
        }
    }

    private static void collectDayQuote(List<Quote> hourQuotesInsideOneDay, List<Quote> dayQuotes) {
        List<Quote> dayQuotesSorted = new ArrayList<>(hourQuotesInsideOneDay);
        Collections.sort(dayQuotesSorted, Comparator.comparing(Quote::getTimestamp));
        long timestamp = dayQuotesSorted.get(0).getTimestamp();
        double high = dayQuotesSorted.stream().mapToDouble(q -> q.getHigh()).max().getAsDouble();
        double open = dayQuotesSorted.get(0).getOpen();
        double close = dayQuotesSorted.get(dayQuotesSorted.size() - 1).getClose();
        double low = dayQuotesSorted.stream().mapToDouble(q -> q.getLow()).min().getAsDouble();
        double volume = dayQuotesSorted.stream().mapToDouble(q -> q.getVolume()).sum();
        Quote dayQuote = new Quote(timestamp, Numbers.round(high), open, close, Numbers.round(low), volume);
        dayQuotes.add(dayQuote);
        hourQuotesInsideOneDay.clear();
    }

}
