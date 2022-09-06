package com.kishlaly.ta.model.indicators

import com.kishlaly.ta.model.AbstractModel

data class Keltner(override val timestamp: Long,
                   val low: Double,
                   val middle: Double,
                   val top: Double) : AbstractModel(timestamp) {

    override fun valuesPresent() = low != Double.NaN && middle != Double.NaN && top != Double.NaN

}