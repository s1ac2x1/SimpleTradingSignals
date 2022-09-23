package com.kishlaly.ta.model.indicators

import com.kishlaly.ta.model.AbstractModel

class Keltner(
    timestamp: Long,
    val low: Double,
    val middle: Double,
    val top: Double
) : AbstractModel(timestamp) {

    override fun valuesPresent() = low != Double.NaN && middle != Double.NaN && top != Double.NaN

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Keltner

        if (low != other.low) return false
        if (middle != other.middle) return false
        if (top != other.top) return false

        return true
    }

    override fun hashCode(): Int {
        var result = low.hashCode()
        result = 31 * result + middle.hashCode()
        result = 31 * result + top.hashCode()
        return result
    }

}