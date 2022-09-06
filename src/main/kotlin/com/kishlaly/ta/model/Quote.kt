package com.kishlaly.ta.model

import kotlin.Double.Companion.NaN

class Quote(
        timestamp: Long,
        val high: Double,
        val open: Double,
        val close: Double,
        val low: Double,
        val volume: Double) : AbstractModel(timestamp), java.io.Serializable {


    override fun valuesPresent(): Boolean {
        return open != NaN
                && close != NaN
                && low != NaN
                && high != NaN
                && volume != NaN
    }

}