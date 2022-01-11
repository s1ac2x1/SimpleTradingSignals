package com.kishlaly.ta;

import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.utils.Dates;
import com.kishlaly.ta.utils.Numbers;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.kishlaly.ta.cache.CacheReader.loadQuotesFromCache;

public class Test {

    public static void main(String[] args) {
        List<Quote> quotes = loadQuotesFromCache("MCK");
        List<Quote> weeklyQuotes = new ArrayList<>();
        AtomicBoolean foundMonday = new AtomicBoolean(false);
        AtomicBoolean foundFriday = new AtomicBoolean(false);
        List<Quote> duringWeek = new ArrayList<>();
        AtomicReference<Quote> startWeekQuote = new AtomicReference<>(null);
        AtomicReference<Quote> endWeekQuote = new AtomicReference<>(null);
        quotes.forEach(quote -> {
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
    }

}
