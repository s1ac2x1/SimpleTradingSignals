package com.kishlaly.ta.utils

import com.kishlaly.ta.model.AbstractModelJava
import com.kishlaly.ta.model.Quote
import com.kishlaly.ta.model.TimeframeJava
import org.ta4j.core.BarSeries
import org.ta4j.core.BaseBarSeries
import java.time.Duration
import java.time.ZonedDateTime

class Bars {

    companion object {

        fun build(quotes: List<Quote>): BarSeries {
            val initialBarSeries: BarSeries = BaseBarSeries()
            quotes.forEach {
                try {
                    val timestamp: ZonedDateTime = Dates.getTimeInExchangeZone(it.timestamp, AbstractModelJava.exchangeTimezome)
                    initialBarSeries.addBar(
                            Duration.ofMinutes(BarsJava.getBarDurationInMinutes().toLong()),
                            timestamp,
                            it.open,
                            it.high,
                            it.low,
                            it.close,
                            it.volume)
                } catch (e: Exception) {
                    println(e.message)
                }
            }
            return initialBarSeries
        }

        fun getBarDurationInMinutes(): Int {
            return when (Context.timeframe) {
                TimeframeJava.DAY -> Context.workingTime
                TimeframeJava.WEEK -> Context.workingTime * 5
                TimeframeJava.HOUR -> 60
                else -> 60
            }
        }

    }

}