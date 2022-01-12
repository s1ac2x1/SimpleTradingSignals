package com.kishlaly.ta;

import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.Timeframe;
import com.kishlaly.ta.utils.*;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.EMAIndicator;

import java.time.ZonedDateTime;
import java.util.List;

import static com.kishlaly.ta.cache.CacheReader.loadQuotesFromCache;

public class Test {

    public static void main(String[] args) {
//        List<Quote> dailyQuotes = loadQuotesFromCache("TER");
//        List<Quote> weeklyQuotes = Quotes.dailyToWeekly(dailyQuotes);
//        Context.timeframe = Timeframe.WEEK;
//        BarSeries bars = Bars.build(weeklyQuotes);
//        EMAIndicator ema26 = IndicatorUtils.buildEMA(bars, 26);
//        System.out.println(ema26);
        System.out.println(Numbers.percent(21, 200));
    }

}
