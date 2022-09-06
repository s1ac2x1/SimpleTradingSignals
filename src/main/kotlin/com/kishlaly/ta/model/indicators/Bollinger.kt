package com.kishlaly.ta.model.indicators

import com.kishlaly.ta.model.AbstractModel

class Bollinger(
        timestam: Long,
        val bottom: Double,
        val middle: Double,
        val top: Double) : AbstractModel(timestam) {
}