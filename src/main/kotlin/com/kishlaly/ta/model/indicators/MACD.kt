package com.kishlaly.ta.model.indicators

import com.kishlaly.ta.model.AbstractModel

data class MACD(override val timestamp: Long,
                val macd: Double,
                val signal: Double,
                val histogram: Double) : AbstractModel(timestamp) {

    override fun valuesPresent() = macd != Double.NaN && signal != Double.NaN && histogram != Double.NaN

}