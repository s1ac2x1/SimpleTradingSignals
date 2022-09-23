package com.kishlaly.ta.model.indicators

import com.kishlaly.ta.model.AbstractModel

class EMA(timestamp: Long, val value: Double) : AbstractModel(timestamp) {

    override fun valuesPresent() = value != Double.NaN

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EMA

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

}