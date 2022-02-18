package com.kishlaly.ta;

import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.Timeframe;
import com.kishlaly.ta.utils.Bars;
import com.kishlaly.ta.utils.Context;
import com.kishlaly.ta.utils.Dates;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.ATRIndicator;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static com.kishlaly.ta.cache.CacheReader.loadQuotesFromCache;

public class Test {

    public static void main(String[] args) {
        Context.aggregationTimeframe = Timeframe.DAY;
        List<Quote> quotes = loadQuotesFromCache("LMT");
        Long timestamp = quotes.get(0).getTimestamp();
        int lastQuoteYear = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.of(Context.myTimezone)).getYear();
        int currentYear = LocalDateTime.ofInstant(Instant.now(), ZoneId.of(Context.myTimezone)).getYear();
        System.out.println(lastQuoteYear);
        System.out.println(currentYear);
    }

}
