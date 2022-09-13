package com.kishlaly.ta.analyze.testing.sl

import com.kishlaly.ta.model.SymbolData

abstract class StopLossStrategy(var config: Any? = null, var isVolatile: Boolean = false) {

    abstract fun calculate(data: SymbolData, signalIndex: Int): Double

    abstract override fun toString(): String

}