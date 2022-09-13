package com.kishlaly.ta.analyze.testing.sl

import com.kishlaly.ta.model.SymbolData

abstract class StopLossStrategy(
    val config: Any? = null,
    val isVolatile: Boolean = false
) {

    abstract fun calculate(data: SymbolData, signalIndex: Int): Double

    abstract override fun toString(): String

}