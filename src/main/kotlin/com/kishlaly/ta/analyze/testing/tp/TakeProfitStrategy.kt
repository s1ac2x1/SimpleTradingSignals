package com.kishlaly.ta.analyze.testing.tp

import com.kishlaly.ta.model.SymbolData

abstract class TakeProfitStrategy(
    val config: Any? = null,
    val isVolatile: Boolean = false,
    val enabled: Boolean = true
) {

    abstract fun calculate(data: SymbolData, signalIndex: Int): Double

    abstract override fun toString(): String

}