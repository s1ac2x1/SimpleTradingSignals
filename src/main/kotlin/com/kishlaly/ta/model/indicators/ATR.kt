package com.kishlaly.ta.model.indicators

import com.kishlaly.ta.model.AbstractModel

class ATR(timestamp: Long, val value: Double) : AbstractModel(timestamp) {

    fun valuesPresent(): Boolean {
        return value != Double.NaN
    }

}