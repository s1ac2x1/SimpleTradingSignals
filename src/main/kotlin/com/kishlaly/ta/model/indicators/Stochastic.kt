package com.kishlaly.ta.model.indicators

import com.kishlaly.ta.model.AbstractModel

class Stochastic(
    timestamp: Long,
    val slowD: Double,
    val slowK: Double
) : AbstractModel(timestamp) {

    override fun valuesPresent() = slowD != Double.NaN && slowK != Double.NaN

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Stochastic

        if (slowD != other.slowD) return false
        if (slowK != other.slowK) return false

        return true
    }

    override fun hashCode(): Int {
        var result = slowD.hashCode()
        result = 31 * result + slowK.hashCode()
        return result
    }

}