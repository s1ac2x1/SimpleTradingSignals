package com.kishlaly.ta;

import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.Timeframe;
import com.kishlaly.ta.utils.Bars;
import com.kishlaly.ta.utils.Context;
import com.kishlaly.ta.utils.Dates;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.ATRIndicator;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

import static com.kishlaly.ta.cache.CacheReader.loadQuotesFromCache;

public class Test {

    public static void main(String[] args) {
        Context.aggregationTimeframe = Timeframe.DAY;
        List<Quote> quotes = loadQuotesFromCache("MAS");
        BarSeries barSeries = Bars.build(quotes);
        ATRIndicator atrIndicator = new ATRIndicator(barSeries, 22);
        System.out.println(atrIndicator);
    }

}
