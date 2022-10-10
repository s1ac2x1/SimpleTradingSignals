package com.kishlaly.ta.utils

import com.kishlaly.ta.cache.IndicatorsInMemoryCache
import com.kishlaly.ta.config.Context
import com.kishlaly.ta.model.AbstractModel
import com.kishlaly.ta.model.Quote
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.indicators.*
import org.ta4j.core.BarSeries
import org.ta4j.core.BaseBarSeries
import org.ta4j.core.indicators.*
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator
import org.ta4j.core.indicators.keltner.KeltnerChannelLowerIndicator
import org.ta4j.core.indicators.keltner.KeltnerChannelMiddleIndicator
import org.ta4j.core.indicators.keltner.KeltnerChannelUpperIndicator
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator

class IndicatorUtils {

    companion object {

        fun buildEMA(symbol: String, quotes: List<Quote>, period: Int): List<EMA> {
            val cached = IndicatorsInMemoryCache.getEMA(symbol, Context.timeframe.get(), period)
            return if (!cached.isEmpty()) {
                cached
            } else {
                val barSeries = Bars.build(quotes)
                val closePriceIndicator = ClosePriceIndicator(barSeries)
                val ema = EMAIndicator(closePriceIndicator, period)
                var collector: MutableList<EMA> = mutableListOf()
                for (i in 0 until ema.barSeries.barCount) {
                    collector.add(EMA(quotes[i].timestamp, ema.getValue(i).doubleValue()))
                }

                collector = collector.filter { it.valuesPresent() }.sortedBy { it.timestamp }.toMutableList()
                var result = trimToDate<EMA>(collector)
                IndicatorsInMemoryCache.putEMA(symbol, Context.timeframe.get(), period, result)
                result
            }
        }

        fun buildMACDHistogram(symbol: String, quotes: List<Quote>): List<MACD> {
            val cached = IndicatorsInMemoryCache.getMACD(symbol, Context.timeframe.get())
            return if (!cached.isEmpty()) {
                cached
            } else {
                var collector = mutableListOf<MACD>()
                val barSeries = Bars.build(quotes)

                val closePrice = ClosePriceIndicator(barSeries)
                val macd = MACDIndicator(closePrice)

                val macdSeries = BaseBarSeries()
                for (i in 0 until barSeries.barCount) {
                    macdSeries.addBar(
                        barSeries.getBar(i).endTime, 0.0, 0.0, 0.0,
                        macd.getValue(i).doubleValue(), 0.0
                    )
                }
                val macdSignalIndicator = ClosePriceIndicator(macdSeries)
                val macdSignal = EMAIndicator(macdSignalIndicator, 9)

                for (i in 0 until barSeries.barCount) {
                    val histogram = macd.getValue(i).minus(macdSignal.getValue(i)).doubleValue()
                    collector.add(MACD(quotes[i].timestamp, 0.0, 0.0, histogram))
                }
                collector = collector.filter { it.valuesPresent() }.sortedBy { it.timestamp }.toMutableList()
                val result = trimToDate(collector)
                IndicatorsInMemoryCache.putMACD(symbol, Context.timeframe.get(), result)
                result
            }
        }

        fun buildKeltnerChannels(symbol: String, quotes: List<Quote>): List<Keltner> {
            val cached = IndicatorsInMemoryCache.getKeltner(symbol, Context.timeframe.get())
            return if (!cached.isEmpty()) {
                cached
            } else {
                val barSeries = Bars.build(quotes)
                val middle = KeltnerChannelMiddleIndicator(barSeries, 20)
                val low = KeltnerChannelLowerIndicator(middle, 2.0, 10)
                val top = KeltnerChannelUpperIndicator(middle, 2.0, 10)

                var collector = mutableListOf<Keltner>()
                for (i in quotes.indices) {
                    collector.add(
                        Keltner(
                            quotes[i].timestamp,
                            low.getValue(i).doubleValue(),
                            middle.getValue(i).doubleValue(),
                            top.getValue(i).doubleValue()
                        )
                    )
                }
                collector = collector.filter { it.valuesPresent() }.sortedBy { it.timestamp }.toMutableList()
                val result = trimToDate(collector)
                IndicatorsInMemoryCache.putKeltner(symbol, Context.timeframe.get(), result)
                result
            }
        }

        fun buildATR(symbol: String, quotes: List<Quote>, barCount: Int): List<ATR> {
            val cached = IndicatorsInMemoryCache.getATR(symbol, Context.timeframe.get(), barCount)
            return if (!cached.isEmpty()) {
                cached
            } else {
                var collector = mutableListOf<ATR>()
                val barSeries = Bars.build(quotes)
                val atrIndicator = ATRIndicator(barSeries, barCount)
                for (i in quotes.indices) {
                    collector.add(ATR(quotes[i].timestamp, atrIndicator.getValue(i).doubleValue()))
                }
                collector = collector.filter { it.valuesPresent() }.sortedBy { it.timestamp }.toMutableList()
                val result = trimToDate(collector)
                IndicatorsInMemoryCache.putATR(symbol, Context.timeframe.get(), barCount, result)
                emptyList()
            }
        }

        fun buildStochastic(symbol: String, quotes: List<Quote>): List<Stochastic> {
            val cached = IndicatorsInMemoryCache.getStoch(symbol, Context.timeframe.get())
            return if (!cached.isEmpty()) {
                cached
            } else {
                var collector = mutableListOf<Stochastic>()
                val barSeries = Bars.build(quotes)
                val stochK = StochasticOscillatorKIndicator(barSeries, 14)
                val stochD = StochasticOscillatorDIndicator(stochK)
                for (i in quotes.indices) {
                    try {
                        collector.add(
                            Stochastic(
                                quotes[i].timestamp,
                                stochD.getValue(i).doubleValue(),
                                stochK.getValue(i).doubleValue()
                            )
                        )
                    } catch (e: NumberFormatException) {
                    }
                }
                collector = collector.filter { it.valuesPresent() }.sortedBy { it.timestamp }.toMutableList()
                var result = trimToDate(collector)
                IndicatorsInMemoryCache.putStoch(symbol, Context.timeframe.get(), result)
                result
            }

        }

        fun buildBollingerBands(symbol: String, quotes: List<Quote>): List<Bollinger> {
            val cached = IndicatorsInMemoryCache.getBollinger(symbol, Context.timeframe.get())
            return if (!cached.isEmpty()) {
                cached
            } else {
                var collector = mutableListOf<Bollinger>()
                val barSeries = Bars.build(quotes)
                val closePriceIndicator = ClosePriceIndicator(barSeries)
                val sma20 = SMAIndicator(closePriceIndicator, 20)
                val standartDeviation = StandardDeviationIndicator(closePriceIndicator, 20)
                val middle = BollingerBandsMiddleIndicator(sma20)
                val bottom = BollingerBandsLowerIndicator(middle, standartDeviation)
                val top = BollingerBandsUpperIndicator(middle, standartDeviation)
                for (i in quotes.indices) {
                    try {
                        collector.add(
                            Bollinger(
                                quotes[i].timestamp,
                                bottom.getValue(i).doubleValue(),
                                middle.getValue(i).doubleValue(),
                                top.getValue(i).doubleValue()
                            )
                        )
                    } catch (e: NumberFormatException) {
                    }
                }
                collector = collector.filter { it.valuesPresent() }.sortedBy { it.timestamp }.toMutableList()
                val result = trimToDate(collector)
                result
            }
        }

        //TODO something is wrong here...
        open fun buildEFI(symbol: String, quotes: List<Quote>): List<ElderForceIndex> {
            val cached = IndicatorsInMemoryCache.getEFI(symbol, Context.timeframe.get())
            return if (!cached.isEmpty()) {
                cached
            } else {
                var collector = mutableListOf<ElderForceIndex>()
                val quoteSeries = Bars.build(quotes)
                val efiSeries: BarSeries = BaseBarSeries()
                for (i in 0 until quoteSeries.barCount - 1) {
                    val todayQuote = quotes[i + 1]
                    val yesterdayQuote = quotes[i]
                    val efiValue = (todayQuote.close - yesterdayQuote.close) * todayQuote.volume
                    efiSeries.addBar(quoteSeries.getBar(i + 1).endTime, 0.0, 0.0, 0.0, efiValue, 0.0)
                }
                val efiClosePriceIndicator = ClosePriceIndicator(efiSeries)
                val efiEMA13 = EMAIndicator(efiClosePriceIndicator, 13)
                for (i in 0 until efiSeries.barCount) {
                    val efiSmoothed = efiEMA13.getValue(i).doubleValue()
                    val timestamp = efiSeries.getBar(i).endTime.toEpochSecond()
                    collector.add(ElderForceIndex(timestamp, efiSmoothed))
                }
//            for (int i = 0; i < quotes.size() - 1; i++) {
//                Quote todayQuote = quotes.get(i + 1);
//                Quote yesterdayQuote = quotes.get(i);
//                double efiValue = (todayQuote.getClose() - yesterdayQuote.getClose()) * todayQuote.getVolume();
//                result.add(new ElderForceIndex(todayQuote.getTimestamp(), efiValue));
//            }
                val result = collector.filter { it.valuesPresent() }.sortedBy { it.timestamp }
                IndicatorsInMemoryCache.putEFI(symbol, Context.timeframe.get(), result)
                result
            }
        }

        fun emaAscending(ema: List<EMA>, atLeast: Int, fromLast: Int): Boolean {
            if (fromLast < 2) {
                throw RuntimeException("EMA ascending check: required at least 2 values")
            }
            var ascendingCount = 0
            for (i in ema.size - fromLast until ema.size - 1) {
                val curr = ema[i]
                val next = ema[i + 1]
                if (next.value > curr.value) {
                    ascendingCount++
                }
            }
            return ascendingCount >= atLeast
        }

        fun trim(screen: SymbolData) {
            val trimmedIndicators = mutableMapOf<Indicator, List<AbstractModel>>()
            screen.indicators.forEach { indicator, values ->
                if (values.isNullOrEmpty()) {
                    trimmedIndicators.put(indicator, mutableListOf())
                } else {
                    trimmedIndicators[indicator] =
                        values.subList(values.size - Quotes.resolveMinBarsCount(screen.timeframe), values.size)
                }
            }
        }

        private fun <T : AbstractModel> trimToDate(src: MutableList<T>): MutableList<T> {
            return if (Context.trimToDate != null) {
                val filterAfter = Dates.shortDateToZoned(Context.trimToDate!!)
                src.filter { it.timestamp <= filterAfter.toEpochSecond() }.toMutableList()
            } else {
                src
            }
        }


    }

}