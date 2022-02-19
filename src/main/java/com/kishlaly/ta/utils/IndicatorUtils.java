package com.kishlaly.ta.utils;

import com.kishlaly.ta.cache.IndicatorsInMemoryCache;
import com.kishlaly.ta.cache.QuotesInMemoryCache;
import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.indicators.*;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.indicators.*;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.keltner.KeltnerChannelLowerIndicator;
import org.ta4j.core.indicators.keltner.KeltnerChannelMiddleIndicator;
import org.ta4j.core.indicators.keltner.KeltnerChannelUpperIndicator;

import java.util.ArrayList;
import java.util.List;

public class IndicatorUtils {

    public static List<EMA> buildEMA(String symbol, int period) {
        List<EMA> fromCache = IndicatorsInMemoryCache.getEMA(symbol, Context.timeframe, period);
        if (fromCache != null) {
            return fromCache;
        } else {
            List<Quote> quotes = QuotesInMemoryCache.get(symbol, Context.timeframe);
            BarSeries barSeries = Bars.build(quotes);
            ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(barSeries);
            EMAIndicator ema = new EMAIndicator(closePriceIndicator, period);
            List<EMA> result = new ArrayList<>();
            for (int i = 0; i < ema.getBarSeries().getBarCount(); i++) {
                result.add(new EMA(quotes.get(i).getTimestamp(), ema.getValue(i).doubleValue()));
            }
            IndicatorsInMemoryCache.putEMA(symbol, Context.timeframe, period, result);
            return result;
        }
    }

    public static List<MACD> buildMACDHistogram(String symbol) {
        // TODO IndicatorsInMemoryCache
        List<MACD> result = new ArrayList<>();
        List<Quote> quotes = QuotesInMemoryCache.get(symbol, Context.timeframe);
        BarSeries barSeries = Bars.build(quotes);

        ClosePriceIndicator closePrice = new ClosePriceIndicator(barSeries);
        MACDIndicator macd = new MACDIndicator(closePrice);

        BarSeries macdSeries = new BaseBarSeries();
        for (int i = 0; i < barSeries.getBarCount(); i++) {
            macdSeries.addBar(barSeries.getBar(i).getEndTime(), 0d, 0d, 0d,
                    macd.getValue(i).doubleValue(), 0d);
        }
        ClosePriceIndicator macdSignalIndicator = new ClosePriceIndicator(macdSeries);
        EMAIndicator macdSignal = new EMAIndicator(macdSignalIndicator, 9);

        for (int i = 0; i < barSeries.getBarCount(); i++) {
            double histogram = macd.getValue(i).minus(macdSignal.getValue(i)).doubleValue();
            result.add(new MACD(quotes.get(i).getTimestamp(), 0d, 0d, histogram));
        }

        return result;
    }

    public static List<Keltner> buildKeltnerChannels(String symbol) {
        // TODO IndicatorsInMemoryCache
        List<Quote> quotes = QuotesInMemoryCache.get(symbol, Context.timeframe);
        BarSeries barSeries = Bars.build(quotes);
        KeltnerChannelMiddleIndicator middle = new KeltnerChannelMiddleIndicator(barSeries, 20);
        KeltnerChannelLowerIndicator low = new KeltnerChannelLowerIndicator(middle, 2, 10);
        KeltnerChannelUpperIndicator top = new KeltnerChannelUpperIndicator(middle, 2, 10);
        List<Keltner> result = new ArrayList<>();
        for (int i = 0; i < quotes.size(); i++) {
            result.add(new Keltner(quotes.get(i).getTimestamp(), low.getValue(i).doubleValue(), middle.getValue(i).doubleValue(), top.getValue(i).doubleValue()));
        }
        return result;
    }

    public static List<ATR> buildATR(String symbol, int barCount) {
        // TODO IndicatorsInMemoryCache
        List<Quote> quotes = QuotesInMemoryCache.get(symbol, Context.timeframe);
        List<ATR> result = new ArrayList<>();
        BarSeries barSeries = Bars.build(quotes);
        ATRIndicator atrIndicator = new ATRIndicator(barSeries, barCount);
        for (int i = 0; i < quotes.size(); i++) {
            result.add(new ATR(quotes.get(i).getTimestamp(), atrIndicator.getValue(i).doubleValue()));
        }
        return result;
    }

    public static List<Stoch> buildStochastic(String symbol) {
        // TODO IndicatorsInMemoryCache
        List<Stoch> result = new ArrayList<>();
        List<Quote> quotes = QuotesInMemoryCache.get(symbol, Context.timeframe);
        BarSeries barSeries = Bars.build(quotes);
        StochasticOscillatorKIndicator stochK = new StochasticOscillatorKIndicator(barSeries, 14);
        StochasticOscillatorDIndicator stochD = new StochasticOscillatorDIndicator(stochK);
        for (int i = 0; i < quotes.size(); i++) {
            try {
                result.add(new Stoch(quotes.get(i).getTimestamp(), stochD.getValue(i).doubleValue(), stochK.getValue(i).doubleValue()));
            } catch (NumberFormatException e) {
            }
        }
        return result;
    }

}
