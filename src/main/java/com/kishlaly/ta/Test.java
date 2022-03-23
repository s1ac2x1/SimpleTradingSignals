package com.kishlaly.ta;

import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.Timeframe;
import com.kishlaly.ta.model.indicators.Bollinger;
import com.kishlaly.ta.utils.Bars;
import com.kishlaly.ta.utils.Context;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;

import java.util.ArrayList;
import java.util.List;

import static com.kishlaly.ta.cache.CacheReader.loadQuotesFromDiskCache;

public class Test {

    public static void main(String[] args) {
        indicators();
    }

    private static void indicators() {
        Context.aggregationTimeframe = Timeframe.DAY;
        Context.timeframe = Timeframe.DAY;
        String symbol = "AAPL";
        List<Quote> quotes = loadQuotesFromDiskCache(symbol);

        BarSeries barSeries = Bars.build(quotes);
        ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(barSeries);
        SMAIndicator sma20 = new SMAIndicator(closePriceIndicator, 20);
        StandardDeviationIndicator standartDeviation = new StandardDeviationIndicator(closePriceIndicator, 2);

        BollingerBandsMiddleIndicator middle = new BollingerBandsMiddleIndicator(sma20);
        BollingerBandsLowerIndicator bottom = new BollingerBandsLowerIndicator(middle, standartDeviation);
        BollingerBandsUpperIndicator top = new BollingerBandsUpperIndicator(middle, standartDeviation);

        List<Bollinger> result = new ArrayList<>();
        for (int i = 0; i < quotes.size(); i++) {
            try {
                result.add(new Bollinger(quotes.get(i).getTimestamp(), bottom.getValue(i).doubleValue(), middle.getValue(i).doubleValue(), top.getValue(i).doubleValue()));
            } catch (NumberFormatException e) {
            }
        }
        System.out.println(result);
    }

}
