package com.kishlaly.ta.utils

import java.math.BigDecimal

fun Double.round(): Double {
    return if (this == Double.NaN || this == 0.0) {
        0.0
    } else BigDecimal.valueOf(this).setScale(2, BigDecimal.ROUND_UP).toDouble()
}

fun Double.roundDown(): Double {
    return if (this == Double.NaN || this == 0.0) {
        0.0
    } else BigDecimal.valueOf(this).setScale(2, BigDecimal.ROUND_DOWN).toDouble()
}

