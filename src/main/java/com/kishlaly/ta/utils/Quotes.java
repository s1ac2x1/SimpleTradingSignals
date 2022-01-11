package com.kishlaly.ta.utils;

import com.kishlaly.ta.model.Quote;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Quotes {

    public static List<Quote> dailyToWeekly(List<Quote> dailyQuotes) {
        List<Quote> weeklyQuotes = new ArrayList<>();
        Set<Quote> week = new HashSet<>();
        List<Quote> weekSorted = new ArrayList<>();
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

}
