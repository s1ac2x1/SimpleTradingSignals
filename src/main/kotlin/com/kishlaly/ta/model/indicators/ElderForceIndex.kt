package com.kishlaly.ta.model.indicators

import com.kishlaly.ta.model.AbstractModel

data class ElderForceIndex(override val timestamp: Long, val value: Double) : AbstractModel(timestamp) {

    override fun valuesPresent() = value != Double.NaN

}