package com.kishlaly.ta.utils

import com.kishlaly.ta.cache.IndicatorsInMemoryCache
import com.kishlaly.ta.config.Context
import com.kishlaly.ta.model.AbstractModel
import com.kishlaly.ta.model.Quote
import com.kishlaly.ta.model.indicators.EMA
import org.ta4j.core.indicators.EMAIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator

class IndicatorUtils {

    companion object {

        fun buildEMA(symbol: String, quotes: List<Quote>, period: Int): List<EMA> {
            val cached = IndicatorsInMemoryCache.getEMA(symbol, Context.timeframe, period)
            if (!cached.isEmpty()) {
                return cached
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
                return result
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