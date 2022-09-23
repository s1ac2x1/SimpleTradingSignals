package com.kishlaly.ta.model

import com.kishlaly.ta.model.indicators.Bollinger
import com.kishlaly.ta.model.indicators.EMA
import com.kishlaly.ta.model.indicators.Keltner
import kotlin.Double.Companion.NaN

class Quote(
    timestamp: Long,
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Quote

        if (high != other.high) return false
        if (open != other.open) return false
        if (close != other.close) return false
        if (low != other.low) return false
        if (volume != other.volume) return false

        return true
    }

    override fun hashCode(): Int {
        var result = high.hashCode()
        result = 31 * result + open.hashCode()
        result = 31 * result + close.hashCode()
        result = 31 * result + low.hashCode()
        result = 31 * result + volume.hashCode()
        return result
    }

    companion object {
        fun NaN() = Quote(-1, -1.0, -1.0, -1.0, -1.0, -1.0)
    }

}

infix fun Quote.crosses(ema: EMA) = low <= ema.value && high >= ema.value

infix fun Quote.below(ema: EMA) = low < ema.value && high < ema.value

infix fun Quote.above(ema: EMA) = low > ema.value && high > ema.value

infix fun Quote.crossesBollingerBottom(bollinger: Bollinger) = low <= bollinger.bottom && high >= bollinger.bottom

infix fun Quote.belowBollingerBottom(bollinger: Bollinger) = low < bollinger.bottom && high < bollinger.bottom

infix fun Quote.crossesKeltnerBottom(keltner: Keltner) = low <= keltner.low && high >= keltner.low

fun Quote.isGreen() = close > open