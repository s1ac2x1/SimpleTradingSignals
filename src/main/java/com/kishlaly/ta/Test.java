package com.kishlaly.ta;

import com.kishlaly.ta.utils.Numbers;

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
