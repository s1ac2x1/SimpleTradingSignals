package com.kishlaly.ta.utils;

import com.kishlaly.ta.model.HistogramQuote;
import com.kishlaly.ta.model.Quote;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import java.util.ArrayList;
import java.util.List;

public class IndicatorUtils {

    public static EMAIndicator buildEMA(BarSeries barSeries, int period) {
        ClosePriceIndicator screenOneclosePrice = new ClosePriceIndicator(barSeries);
        EMAIndicator ema = new EMAIndicator(screenOneclosePrice, period);
        return ema;
    }

    public static List<HistogramQuote> buildMACDHistogram(BarSeries barSeries, List<Quote> quotes) {
        ClosePriceIndicator closePrice = new ClosePriceIndicator(barSeries);
        MACDIndicator macd = new MACDIndicator(closePrice);

        BarSeries macdSeries = new BaseBarSeries();
        for (int i = 0; i < barSeries.getBarCount(); i++) {
            macdSeries.addBar(barSeries.getBar(i).getEndTime(), 0d, 0d, 0d,
                    macd.getValue(i).doubleValue(), 0d);
        }
        ClosePriceIndicator macdSignalIndicator = new ClosePriceIndicator(macdSeries);
        EMAIndicator macdSignal = new EMAIndicator(macdSignalIndicator, 9);

        List<HistogramQuote> histogramQuotes = new ArrayList<>();
        for (int i = 0; i < barSeries.getBarCount(); i++) {
            double histogram = macd.getValue(i).minus(macdSignal.getValue(i)).doubleValue();
            histogramQuotes.add(new HistogramQuote(histogram, quotes.get(i)));
        }

        return histogramQuotes;
    }

}
