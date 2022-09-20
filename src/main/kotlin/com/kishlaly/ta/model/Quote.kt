package com.kishlaly.ta.model

import com.kishlaly.ta.model.indicators.Bollinger
import com.kishlaly.ta.model.indicators.EMA
import com.kishlaly.ta.model.indicators.Keltner
import kotlin.Double.Companion.NaN

data class Quote(
    override val timestamp: Long,
    val high: Double,
    val open: Double,
    val close: Double,
    val low: Double,
    val volume: Double
) : AbstractModel(timestamp), java.io.Serializable {

    override fun valuesPresent(): Boolean {
        return open != NaN
                && close != NaN
                && low != NaN
                && high != NaN
                && volume != NaN
    }

    companion object {
        fun NaN() = Quote(-1, -1.0, -1.0, -1.0, -1.0, -1.0)
    }

}

fun Quote.isCrossesEMA(ema: EMA) = low <= ema.value && high >= ema.value

fun Quote.isBelowEMA(ema: EMA) = low < ema.value && high < ema.value

fun Quote.isAboveEMA(ema: EMA) = low > ema.value && high > ema.value

fun Quote.isCrossesBollingerBottom(bollinger: Bollinger) = low <= bollinger.bottom && high >= bollinger.bottom

fun Quote.isBelowBollingerBottom(bollinger: Bollinger) = low < bollinger.bottom && high < bollinger.bottom

fun Quote.isCrossesKeltnerBottom(keltner: Keltner) = low <= keltner.low && high >= keltner.low

fun Quote.isGreen() = close > open