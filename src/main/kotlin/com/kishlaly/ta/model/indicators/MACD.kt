package com.kishlaly.ta.model.indicators

import com.kishlaly.ta.model.AbstractModel

class MACD(
    timestamp: Long,
    val macd: Double,
    val signal: Double,
    val histogram: Double
) : AbstractModel(timestamp) {

    override fun valuesPresent() = macd != Double.NaN && signal != Double.NaN && histogram != Double.NaN

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MACD

        if (macd != other.macd) return false
        if (signal != other.signal) return false
        if (histogram != other.histogram) return false

        return true
    }

    override fun hashCode(): Int {
        var result = macd.hashCode()
        result = 31 * result + signal.hashCode()
        result = 31 * result + histogram.hashCode()
        return result
    }

}