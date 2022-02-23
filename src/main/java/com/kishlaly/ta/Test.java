package com.kishlaly.ta;

import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.Timeframe;
import com.kishlaly.ta.model.indicators.EMA;
import com.kishlaly.ta.model.indicators.MACD;
import com.kishlaly.ta.model.indicators.Stoch;
import com.kishlaly.ta.utils.Context;
import com.kishlaly.ta.utils.IndicatorUtils;

import java.util.List;

import static com.kishlaly.ta.cache.CacheReader.loadQuotesFromDiskCache;

public class Test {

    public static void main(String[] args) {
        Context.aggregationTimeframe = Timeframe.DAY;
        Context.timeframe = Timeframe.DAY;
        String symbol = "AAPL";
        List<Quote> quotes = loadQuotesFromDiskCache(symbol);
        List<Stoch> stoches = IndicatorUtils.buildStochastic(symbol, quotes);
        System.out.println(stoches);
    }

}
