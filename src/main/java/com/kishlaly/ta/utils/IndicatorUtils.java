package com.kishlaly.ta.utils;

import com.kishlaly.ta.cache.IndicatorsInMemoryCache;
import com.kishlaly.ta.model.AbstractModelJava;
import com.kishlaly.ta.model.QuoteJava;
import com.kishlaly.ta.model.SymbolDataJava;
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

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.kishlaly.ta.utils.DatesJava.shortDateToZoned;
import static com.kishlaly.ta.utils.Quotes.resolveMinBarsCount;

public class IndicatorUtils {

    public static List<EMAJava> buildEMA(String symbol, List<QuoteJava> quotes, int period) {
        List<EMAJava> cached = IndicatorsInMemoryCache.getEMA(symbol, Context.timeframe, period);
        if (!cached.isEmpty()) {
            return cached;
        } else {
            BarSeries barSeries = BarsJava.build(quotes);
            ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(barSeries);
            EMAIndicator ema = new EMAIndicator(closePriceIndicator, period);
            List<EMAJava> result = new ArrayList<>();
            for (int i = 0; i < ema.getBarSeries().getBarCount(); i++) {
                result.add(new EMAJava(quotes.get(i).getTimestamp(), ema.getValue(i).doubleValue()));
            }
            result = result.stream().filter(EMAJava::valuesPresent).collect(Collectors.toList());
            result = trimToDate(result);
            Collections.sort(result, Comparator.comparing(EMAJava::getTimestamp));
            IndicatorsInMemoryCache.putEMA(symbol, Context.timeframe, period, result);
            return result;
        }
    }

    public static List<MACDJava> buildMACDHistogram(String symbol, List<QuoteJava> quotes) {
        List<MACDJava> cached = IndicatorsInMemoryCache.getMACD(symbol, Context.timeframe);
        if (!cached.isEmpty()) {
            return cached;
        } else {
            List<MACDJava> result = new ArrayList<>();
            BarSeries barSeries = BarsJava.build(quotes);

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
                result.add(new MACDJava(quotes.get(i).getTimestamp(), 0d, 0d, histogram));
            }
            result = result.stream().filter(MACDJava::valuesPresent).collect(Collectors.toList());
            result = trimToDate(result);
            Collections.sort(result, Comparator.comparing(MACDJava::getTimestamp));
            IndicatorsInMemoryCache.putMACD(symbol, Context.timeframe, result);
            return result;
        }
    }

    public static List<KeltnerJava> buildKeltnerChannels(String symbol, List<QuoteJava> quotes) {
        List<KeltnerJava> cached = IndicatorsInMemoryCache.getKeltner(symbol, Context.timeframe);
        if (!cached.isEmpty()) {
            return cached;
        } else {
            BarSeries barSeries = BarsJava.build(quotes);
            KeltnerChannelMiddleIndicator middle = new KeltnerChannelMiddleIndicator(barSeries, 20);
            KeltnerChannelLowerIndicator low = new KeltnerChannelLowerIndicator(middle, 2, 10);
            KeltnerChannelUpperIndicator top = new KeltnerChannelUpperIndicator(middle, 2, 10);
            List<KeltnerJava> result = new ArrayList<>();
            for (int i = 0; i < quotes.size(); i++) {
                result.add(new KeltnerJava(quotes.get(i).getTimestamp(), low.getValue(i).doubleValue(), middle.getValue(i).doubleValue(), top.getValue(i).doubleValue()));
            }
            result = result.stream().filter(KeltnerJava::valuesPresent).collect(Collectors.toList());
            result = trimToDate(result);
            Collections.sort(result, Comparator.comparing(KeltnerJava::getTimestamp));
            IndicatorsInMemoryCache.putKeltner(symbol, Context.timeframe, result);
            return result;
        }
    }

    public static List<ATRJava> buildATR(String symbol, List<QuoteJava> quotes, int barCount) {
        List<ATRJava> cached = IndicatorsInMemoryCache.getATR(symbol, Context.timeframe, barCount);
        if (!cached.isEmpty()) {
            return cached;
        } else {
            List<ATRJava> result = new ArrayList<>();
            BarSeries barSeries = BarsJava.build(quotes);
            ATRIndicator atrIndicator = new ATRIndicator(barSeries, barCount);
            for (int i = 0; i < quotes.size(); i++) {
                result.add(new ATRJava(quotes.get(i).getTimestamp(), atrIndicator.getValue(i).doubleValue()));
            }
            result = result.stream().filter(ATRJava::valuesPresent).collect(Collectors.toList());
            result = trimToDate(result);
            Collections.sort(result, Comparator.comparing(ATRJava::getTimestamp));
            IndicatorsInMemoryCache.putATR(symbol, Context.timeframe, barCount, result);
            return result;
        }
    }

    public static List<StochJava> buildStochastic(String symbol, List<QuoteJava> quotes) {
        List<StochJava> cached = IndicatorsInMemoryCache.getStoch(symbol, Context.timeframe);
        if (!cached.isEmpty()) {
            return cached;
        } else {
            List<StochJava> result = new ArrayList<>();
            BarSeries barSeries = BarsJava.build(quotes);
            StochasticOscillatorKIndicator stochK = new StochasticOscillatorKIndicator(barSeries, 14);
            StochasticOscillatorDIndicator stochD = new StochasticOscillatorDIndicator(stochK);
            for (int i = 0; i < quotes.size(); i++) {
                try {
                    result.add(new StochJava(quotes.get(i).getTimestamp(), stochD.getValue(i).doubleValue(), stochK.getValue(i).doubleValue()));
                } catch (NumberFormatException e) {
                }
            }
            result = result.stream().filter(StochJava::valuesPresent).collect(Collectors.toList());
            result = trimToDate(result);
            Collections.sort(result, Comparator.comparing(StochJava::getTimestamp));
            IndicatorsInMemoryCache.putStoch(symbol, Context.timeframe, result);
            return result;
        }
    }

    public static List<BollingerJava> buildBollingerBands(String symbol, List<QuoteJava> quotes) {
        List<BollingerJava> cached = IndicatorsInMemoryCache.getBollinger(symbol, Context.timeframe);
        if (!cached.isEmpty()) {
            return cached;
        } else {
            List<BollingerJava> result = new ArrayList<>();
            BarSeries barSeries = BarsJava.build(quotes);
            ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(barSeries);
            SMAIndicator sma20 = new SMAIndicator(closePriceIndicator, 20);
            StandardDeviationIndicator standartDeviation = new StandardDeviationIndicator(closePriceIndicator, 20);

            BollingerBandsMiddleIndicator middle = new BollingerBandsMiddleIndicator(sma20);
            BollingerBandsLowerIndicator bottom = new BollingerBandsLowerIndicator(middle, standartDeviation);
            BollingerBandsUpperIndicator top = new BollingerBandsUpperIndicator(middle, standartDeviation);

            for (int i = 0; i < quotes.size(); i++) {
                try {
                    result.add(new BollingerJava(quotes.get(i).getTimestamp(), bottom.getValue(i).doubleValue(), middle.getValue(i).doubleValue(), top.getValue(i).doubleValue()));
                } catch (NumberFormatException e) {
                }
            }
            Collections.sort(result, Comparator.comparing(BollingerJava::getTimestamp));
            result = trimToDate(result);
            IndicatorsInMemoryCache.putBollinger(symbol, Context.timeframe, result);
            return result;
        }
    }

    //TODO something is wrong here...
    public static List<ElderForceIndexJava> buildEFI(String symbol, List<QuoteJava> quotes) {
        List<ElderForceIndexJava> cached = IndicatorsInMemoryCache.getEFI(symbol, Context.timeframe);
        if (!cached.isEmpty()) {
            return cached;
        } else {
            List<ElderForceIndexJava> result = new ArrayList<>();
            BarSeries quoteSeries = BarsJava.build(quotes);
            BarSeries efiSeries = new BaseBarSeries();
            for (int i = 0; i < quoteSeries.getBarCount() - 1; i++) {
                QuoteJava todayQuote = quotes.get(i + 1);
                QuoteJava yesterdayQuote = quotes.get(i);
                double efiValue = (todayQuote.getClose() - yesterdayQuote.getClose()) * todayQuote.getVolume();
                efiSeries.addBar(quoteSeries.getBar(i + 1).getEndTime(), 0d, 0d, 0d, efiValue, 0d);
            }
            ClosePriceIndicator efiClosePriceIndicator = new ClosePriceIndicator(efiSeries);
            EMAIndicator efiEMA13 = new EMAIndicator(efiClosePriceIndicator, 13);
            for (int i = 0; i < efiSeries.getBarCount(); i++) {
                double efiSmoothed = efiEMA13.getValue(i).doubleValue();
                Long timestamp = efiSeries.getBar(i).getEndTime().toEpochSecond();
                result.add(new ElderForceIndexJava(timestamp, efiSmoothed));
            }
//            for (int i = 0; i < quotes.size() - 1; i++) {
//                Quote todayQuote = quotes.get(i + 1);
//                Quote yesterdayQuote = quotes.get(i);
//                double efiValue = (todayQuote.getClose() - yesterdayQuote.getClose()) * todayQuote.getVolume();
//                result.add(new ElderForceIndex(todayQuote.getTimestamp(), efiValue));
//            }
            result = trimToDate(result);
            Collections.sort(result, Comparator.comparing(ElderForceIndexJava::getTimestamp));
            IndicatorsInMemoryCache.putEFI(symbol, Context.timeframe, result);
            return result;
        }
    }

    public static boolean emaAscending(List<EMAJava> ema, int atLeast, int fromLast) {
        if (fromLast < 2) {
            throw new RuntimeException("EMA ascending check: required at least 2 values");
        }
        int ascendingCount = 0;
        for (int i = ema.size() - fromLast; i < ema.size() - 1; i++) {
            EMAJava curr = ema.get(i);
            EMAJava next = ema.get(i + 1);
            if (next.getValue() > curr.getValue()) {
                ascendingCount++;
            }
        }
        return ascendingCount >= atLeast;
    }

    public static void trim(SymbolDataJava screen) {
        Map<IndicatorJava, List<? extends AbstractModelJava>> trimmedIndicators = new HashMap();
        screen.indicators.forEach((indicator, values) -> {
            if (values == null || values.isEmpty()) {
                trimmedIndicators.put(indicator, new ArrayList());
            } else {
                trimmedIndicators.put(indicator, values.subList(values.size() - resolveMinBarsCount(screen.timeframe), values.size()));
            }
        });
        screen.indicators = trimmedIndicators;
    }

    private static <T extends AbstractModelJava> List<T> trimToDate(List<T> src) {
        if (Context.trimToDate != null) {
            ZonedDateTime filterAfter = shortDateToZoned(Context.trimToDate);
            return src.stream().filter(ind -> ind.getTimestamp() <= filterAfter.toEpochSecond()).collect(Collectors.toList());
        } else {
            return src;
        }
    }

}
