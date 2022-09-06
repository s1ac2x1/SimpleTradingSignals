package com.kishlaly.ta.utils

import com.kishlaly.ta.cache.IndicatorsInMemoryCache
import com.kishlaly.ta.config.Context
import com.kishlaly.ta.model.AbstractModelJava
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
                var result: MutableList<EMA> = mutableListOf()
                for (i in 0 until ema.barSeries.barCount) {
                    result.add(EMA(quotes[i].timestamp, ema.getValue(i).doubleValue()))
                }

                result = result.filter { it.valuesPresent() }.toMutableList()
                result = trimToDate<EMA>(result)
            }
        }

        private fun <T : AbstractModelJava?> trimToDate(src: MutableList<T>): MutableList<T> {
            return if (ContextJava.trimToDate != null) {
                val filterAfter = DatesJava.shortDateToZoned(ContextJava.trimToDate)
                src.filter { ind: T -> ind!!.timestamp <= filterAfter.toEpochSecond() }.toMutableList()
            } else {
                src
            }
        }


    }

}