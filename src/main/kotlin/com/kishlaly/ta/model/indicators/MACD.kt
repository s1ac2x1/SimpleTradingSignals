package com.kishlaly.ta.model.indicators

import com.kishlaly.ta.model.AbstractModel

class MACD(timestamp: Long,
           val macd: Double,
           val signal: Double,
           val histogram: Double) : AbstractModel(timestamp) {

    fun valuesPresent() = macd != Double.NaN && signal != Double.NaN && histogram != Double.NaN

}