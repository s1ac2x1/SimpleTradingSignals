package com.kishlaly.ta.utils;

import com.kishlaly.ta.cache.IndicatorsInMemoryCache;
import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.SymbolData;
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
import java.util.stream.Collectors;

import static com.kishlaly.ta.utils.Quotes.resolveMinBarsCount;

public class IndicatorUtils {

    public static List<EMA> buildEMA(String symbol, List<Quote> quotes, int period) {
        List<EMA> cached = IndicatorsInMemoryCache.getEMA(symbol, Context.timeframe, period);
        if (!cached.isEmpty()) {
            return cached;
        } else {
            BarSeries barSeries = Bars.build(quotes);
            ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(barSeries);
            EMAIndicator ema = new EMAIndicator(closePriceIndicator, period);
            List<EMA> result = new ArrayList<>();
            for (int i = 0; i < ema.getBarSeries().getBarCount(); i++) {
                result.add(new EMA(quotes.get(i).getTimestamp(), ema.getValue(i).doubleValue()));
            }
            result = result.stream().filter(EMA::valuesPresent).collect(Collectors.toList());
            IndicatorsInMemoryCache.putEMA(symbol, Context.timeframe, period, result);
            return result;
        }
    }

    public static List<MACD> buildMACDHistogram(String symbol, List<Quote> quotes) {
        List<MACD> cached = IndicatorsInMemoryCache.getMACD(symbol, Context.timeframe);
        if (!cached.isEmpty()) {
            return cached;
        } else {
            List<MACD> result = new ArrayList<>();
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
            result = result.stream().filter(MACD::valuesPresent).collect(Collectors.toList());
            IndicatorsInMemoryCache.putMACD(symbol, Context.timeframe, result);
            return result;
        }
    }

    public static List<Keltner> buildKeltnerChannels(String symbol, List<Quote> quotes) {
        List<Keltner> cached = IndicatorsInMemoryCache.getKeltner(symbol, Context.timeframe);
        if (!cached.isEmpty()) {
            return cached;
        } else {
            BarSeries barSeries = Bars.build(quotes);
            KeltnerChannelMiddleIndicator middle = new KeltnerChannelMiddleIndicator(barSeries, 20);
            KeltnerChannelLowerIndicator low = new KeltnerChannelLowerIndicator(middle, 2, 10);
            KeltnerChannelUpperIndicator top = new KeltnerChannelUpperIndicator(middle, 2, 10);
            List<Keltner> result = new ArrayList<>();
            for (int i = 0; i < quotes.size(); i++) {
                result.add(new Keltner(quotes.get(i).getTimestamp(), low.getValue(i).doubleValue(), middle.getValue(i).doubleValue(), top.getValue(i).doubleValue()));
            }
            result = result.stream().filter(Keltner::valuesPresent).collect(Collectors.toList());
            IndicatorsInMemoryCache.putKeltner(symbol, Context.timeframe, result);
            return result;
        }
    }

    public static List<ATR> buildATR(String symbol, List<Quote> quotes, int barCount) {
        List<ATR> cached = IndicatorsInMemoryCache.getATR(symbol, Context.timeframe, barCount);
        if (!cached.isEmpty()) {
            return cached;
        } else {
            List<ATR> result = new ArrayList<>();
            BarSeries barSeries = Bars.build(quotes);
            ATRIndicator atrIndicator = new ATRIndicator(barSeries, barCount);
            for (int i = 0; i < quotes.size(); i++) {
                result.add(new ATR(quotes.get(i).getTimestamp(), atrIndicator.getValue(i).doubleValue()));
            }
            result = result.stream().filter(ATR::valuesPresent).collect(Collectors.toList());
            IndicatorsInMemoryCache.putATR(symbol, Context.timeframe, barCount, result);
            return result;
        }
    }

    public static List<Stoch> buildStochastic(String symbol, List<Quote> quotes) {
        List<Stoch> cached = IndicatorsInMemoryCache.getStoch(symbol, Context.timeframe);
        if (!cached.isEmpty()) {
            return cached;
        } else {
            List<Stoch> result = new ArrayList<>();
            BarSeries barSeries = Bars.build(quotes);
            StochasticOscillatorKIndicator stochK = new StochasticOscillatorKIndicator(barSeries, 14);
            StochasticOscillatorDIndicator stochD = new StochasticOscillatorDIndicator(stochK);
            for (int i = 0; i < quotes.size(); i++) {
                try {
                    result.add(new Stoch(quotes.get(i).getTimestamp(), stochD.getValue(i).doubleValue(), stochK.getValue(i).doubleValue()));
                } catch (NumberFormatException e) {
                }
            }
            result = result.stream().filter(Stoch::valuesPresent).collect(Collectors.toList());
            IndicatorsInMemoryCache.putStoch(symbol, Context.timeframe, result);
            return result;
        }
    }

    public static void trim(SymbolData screen) {
        взять все индикаторы, проверить у каждого
                если нал или пустой - инициализировать
                иначе обрезать
    }

}
