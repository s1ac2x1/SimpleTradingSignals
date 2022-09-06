package com.kishlaly.ta.model.indicators

import com.kishlaly.ta.model.AbstractModel

data class Bollinger(
        override val timestamp: Long,
        val bottom: Double,
        val middle: Double,
        val top: Double) : AbstractModel(timestamp) {

    override fun valuesPresent() = bottom != Double.NaN && middle != Double.NaN && top != Double.NaN

}