package com.kishlaly.ta;

import com.kishlaly.ta.utils.Numbers;
import org.ta4j.core.num.Num;

public class Test {

    public static void main(String[] args) throws Exception {
//        List<Quote> quotes = loadQuotesFromCache("FRC");
//        BarSeries bars = Bars.build(quotes);
        double open = 20454.0;
        double close = 19950.5;
        double roi1 = (close - open) / open * 100;
        double roi2 = (close - open) / open * 100;
        System.out.println(Numbers.round(roi1));
        System.out.println(Numbers.round(roi2));
    }

}
