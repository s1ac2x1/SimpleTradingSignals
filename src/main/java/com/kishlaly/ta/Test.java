package com.kishlaly.ta;

import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.utils.Bars;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.keltner.KeltnerChannelLowerIndicator;
import org.ta4j.core.indicators.keltner.KeltnerChannelMiddleIndicator;
import org.ta4j.core.indicators.keltner.KeltnerChannelUpperIndicator;

import java.util.List;

import static com.kishlaly.ta.cache.CacheReader.loadQuotesFromCache;

public class Test {

    public static void main(String[] args) throws Exception {
        List<Quote> quotes = loadQuotesFromCache("FRC");
        BarSeries bars = Bars.build(quotes);
        KeltnerChannelMiddleIndicator middle = new KeltnerChannelMiddleIndicator(bars, 20);
        KeltnerChannelLowerIndicator low = new KeltnerChannelLowerIndicator(middle, 2, 10);
        KeltnerChannelUpperIndicator upper = new KeltnerChannelUpperIndicator(middle, 2, 10);
        System.out.println(low.getValue(quotes.size() - 1));
        System.out.println(middle.getValue(quotes.size() - 1));
        System.out.println(upper.getValue(quotes.size() - 1));
    }

}
