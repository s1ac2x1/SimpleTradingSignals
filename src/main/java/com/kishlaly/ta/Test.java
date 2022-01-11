package com.kishlaly.ta;

import com.kishlaly.ta.utils.Numbers;

public class Test {

    public static void main(String[] args) throws Exception {
//        List<Quote> quotes = loadQuotesFromCache("FRC");
//        BarSeries bars = Bars.build(quotes);
        double open = 18407.0;
        double close = 17634.0;
        System.out.println(Numbers.roi(open, close));
    }

}
