package com.kishlaly.ta.model.indicators

import com.kishlaly.ta.model.AbstractModel

class Stochastic(timestamp: Long, val slowD: Double, val slowK: Double) : AbstractModel(timestamp) {

    override fun valuesPresent() = slowD != Double.NaN && slowK != Double.NaN
}