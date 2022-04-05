package com.kishlaly.ta.utils;

import com.kishlaly.ta.cache.IndicatorsInMemoryCache;
import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.*;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.indicators.*;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.keltner.KeltnerChannelLowerIndicator;
import org.ta4j.core.indicators.keltner.KeltnerChannelMiddleIndicator;
import org.ta4j.core.indicators.keltner.KeltnerChannelUpperIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;

import java.util.*;
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
            Collections.sort(result, Comparator.comparing(EMA::getTimestamp));
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
            Collections.sort(result, Comparator.comparing(MACD::getTimestamp));
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
            Collections.sort(result, Comparator.comparing(Keltner::getTimestamp));
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
            Collections.sort(result, Comparator.comparing(ATR::getTimestamp));
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
            Collections.sort(result, Comparator.comparing(Stoch::getTimestamp));
            IndicatorsInMemoryCache.putStoch(symbol, Context.timeframe, result);
            return result;
        }
    }

    public static void trim(SymbolData screen) {
        Map<Indicator, List> trimmedIndicators = new HashMap();
        screen.indicators.forEach((indicator, values) -> {
            if (values == null || values.isEmpty()) {
                trimmedIndicators.put(indicator, new ArrayList());
            } else {
                trimmedIndicators.put(indicator, values.subList(values.size() - resolveMinBarsCount(screen.timeframe), values.size()));
            }
        });
        screen.indicators = trimmedIndicators;
    }

    public static List<Bollinger> buildBollingerBands(String symbol, List<Quote> quotes) {
        List<Bollinger> cached = IndicatorsInMemoryCache.getBollinger(symbol, Context.timeframe);
        if (!cached.isEmpty()) {
            return cached;
        } else {
            List<Bollinger> result = new ArrayList<>();
            BarSeries barSeries = Bars.build(quotes);
            ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(barSeries);
            SMAIndicator sma20 = new SMAIndicator(closePriceIndicator, 20);
            StandardDeviationIndicator standartDeviation = new StandardDeviationIndicator(closePriceIndicator, 20);

            BollingerBandsMiddleIndicator middle = new BollingerBandsMiddleIndicator(sma20);
            BollingerBandsLowerIndicator bottom = new BollingerBandsLowerIndicator(middle, standartDeviation);
            BollingerBandsUpperIndicator top = new BollingerBandsUpperIndicator(middle, standartDeviation);

            for (int i = 0; i < quotes.size(); i++) {
                try {
                    result.add(new Bollinger(quotes.get(i).getTimestamp(), bottom.getValue(i).doubleValue(), middle.getValue(i).doubleValue(), top.getValue(i).doubleValue()));
                } catch (NumberFormatException e) {
                }
            }
            IndicatorsInMemoryCache.putBollinger(symbol, Context.timeframe, result);
            return result;
        }
    }

    public static List<ElderForceIndex> buildEFI(String symbol, List<Quote> quotes) {
        List<ElderForceIndex> cached = IndicatorsInMemoryCache.getEFI(symbol, Context.timeframe);
        if (!cached.isEmpty()) {
            return cached;
        } else {
            List<ElderForceIndex> result = new ArrayList<>();
            BarSeries quoteSeries = Bars.build(quotes);
            BarSeries efiSeries = new BaseBarSeries();
            for (int i = 1; i < quoteSeries.getBarCount() - 1; i++) {
                Quote todayQuote = quotes.get(i);
                Quote yesterdayQuote = quotes.get(i - 1);
                double efiValue = (todayQuote.getClose() - yesterdayQuote.getClose()) * todayQuote.getVolume();
                efiSeries.addBar(quoteSeries.getBar(i).getEndTime(), 0d, 0d, 0d, efiValue, 0d);
            }
            ClosePriceIndicator efiClosePriceIndicator = new ClosePriceIndicator(efiSeries);
            EMAIndicator efiEMA13 = new EMAIndicator(efiClosePriceIndicator, 13);
            for (int i = 0; i < efiSeries.getBarCount(); i++) {
                double efiSmoothed = efiEMA13.getValue(i).doubleValue();
                Long timestamp = efiSeries.getBar(i).getEndTime().toEpochSecond();
                result.add(new ElderForceIndex(timestamp, efiSmoothed));
            }
            Collections.sort(result, Comparator.comparing(ElderForceIndex::getTimestamp));
            IndicatorsInMemoryCache.putEFI(symbol, Context.timeframe, result);
            return result;
        }
    }

}
