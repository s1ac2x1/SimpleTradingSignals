package com.kishlaly.ta.utils

import com.kishlaly.ta.cache.IndicatorsInMemoryCache
import com.kishlaly.ta.config.Context
import com.kishlaly.ta.model.AbstractModel
import com.kishlaly.ta.model.Quote
import com.kishlaly.ta.model.indicators.EMA
import com.kishlaly.ta.model.indicators.MACD
import org.ta4j.core.BaseBarSeries
import org.ta4j.core.indicators.EMAIndicator
import org.ta4j.core.indicators.MACDIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator

class IndicatorUtils {

    companion object {

        fun buildEMA(symbol: String, quotes: List<Quote>, period: Int): List<EMA> {
            val cached = IndicatorsInMemoryCache.getEMA(symbol, Context.timeframe, period)
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

                collector = collector.filter { it.valuesPresent() }.toMutableList()
                var result = trimToDate<EMA>(collector).sortedBy { it.timestamp }
                IndicatorsInMemoryCache.putEMA(symbol, Context.timeframe, period, result)
                result
            }
        }

        fun buildMACDHistogram(symbol: String, quotes: List<Quote>): List<MACD> {
            val cached = IndicatorsInMemoryCache.getMACD(symbol, Context.timeframe)
            return if (!cached.isEmpty()) {
                cached
            } else {
                var collector = mutableListOf<MACD>()
                val barSeries = Bars.build(quotes)

                val closePrice = ClosePriceIndicator(barSeries)
                val macd = MACDIndicator(closePrice)

                val macdSeries = BaseBarSeries()
                for (i in 0 until barSeries.barCount) {
                    macdSeries.addBar(barSeries.getBar(i).endTime, 0.0, 0.0, 0.0,
                            macd.getValue(i).doubleValue(), 0.0)
                }
                val macdSignalIndicator = ClosePriceIndicator(macdSeries)
                val macdSignal = EMAIndicator(macdSignalIndicator, 9)

                for (i in 0 until barSeries.barCount) {
                    val histogram = macd.getValue(i).minus(macdSignal.getValue(i)).doubleValue()
                    collector.add(MACD(quotes[i].timestamp, 0.0, 0.0, histogram))
                }
                collector = collector.filter { it.valuesPresent() }.toMutableList()
                val result = trimToDate(collector).sortedBy { it.timestamp }
                IndicatorsInMemoryCache.putMACD(symbol, Context.timeframe, result)
                result
            }
        }

        private fun <T : AbstractModel> trimToDate(src: MutableList<T>): MutableList<T> {
            return if (Context.trimToDate != null) {
                val filterAfter = Dates.shortDateToZoned(Context.trimToDate!!)
                src.filter { ind: T -> ind!!.timestamp <= filterAfter.toEpochSecond() }.toMutableList()
            } else {
                src
            }
        }


    }

}