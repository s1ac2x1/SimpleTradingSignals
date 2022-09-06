package com.kishlaly.ta.model.indicators

import com.kishlaly.ta.model.AbstractModel

class ElderForceIndex(timestamp: Long, val value: Double) : AbstractModel(timestamp) {
    fun valuesPresent() = value != Double.NaN
}