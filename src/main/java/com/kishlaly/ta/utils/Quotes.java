package com.kishlaly.ta.utils;

import com.kishlaly.ta.model.Quote;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Quotes {

    public static List<Quote> convertToWeekly(List<Quote> dailyQuotes) {
        List<Quote> weeklyQuotes = new ArrayList<>();
        AtomicBoolean foundMonday = new AtomicBoolean(false);
        AtomicBoolean foundFriday = new AtomicBoolean(false);
        List<Quote> duringWeek = new ArrayList<>();
        AtomicReference<Quote> startWeekQuote = new AtomicReference<>(null);
        AtomicReference<Quote> endWeekQuote = new AtomicReference<>(null);
        dailyQuotes.forEach(quote -> {
            try {
                ZonedDateTime timeInExchangeZone = Dates.getTimeInExchangeZone(quote.getTimestamp(), Quote.exchangeTimezome);
                DayOfWeek dayOfWeek = timeInExchangeZone.getDayOfWeek();
                if (dayOfWeek == DayOfWeek.MONDAY) {
                    foundMonday.set(true);
                    startWeekQuote.set(quote);
                    duringWeek.clear();
                }
                if (dayOfWeek == DayOfWeek.FRIDAY && foundMonday.get()) {
                    foundFriday.set(true);
                    endWeekQuote.set(quote);
                }
                if (foundMonday.get() || foundFriday.get()) {
                    duringWeek.add(quote);
                }
                if (foundMonday.get() && foundFriday.get()) {
                    long timestamp = startWeekQuote.get().getTimestamp();
                    double high = duringWeek.stream().mapToDouble(q -> q.getHigh()).max().getAsDouble();
                    double open = startWeekQuote.get().getOpen();
                    double close = endWeekQuote.get().getClose();
                    double low = duringWeek.stream().mapToDouble(q -> q.getLow()).min().getAsDouble();
                    double volume = duringWeek.stream().mapToDouble(q -> q.getVolume()).sum();
                    Quote weeklyQuote = new Quote(timestamp, Numbers.round(high), open, close, Numbers.round(low), volume);
                    weeklyQuotes.add(weeklyQuote);
                    foundMonday.set(false);
                    foundFriday.set(false);
                    duringWeek.clear();
                    startWeekQuote.set(null);
                    endWeekQuote.set(null);
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        });
        Collections.sort(weeklyQuotes, Comparator.comparing(Quote::getTimestamp));
        return weeklyQuotes;
    }

}
