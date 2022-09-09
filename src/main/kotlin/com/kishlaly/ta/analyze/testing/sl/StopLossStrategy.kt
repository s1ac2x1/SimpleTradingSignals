package com.kishlaly.ta.analyze.testing.sl

import com.kishlaly.ta.model.SymbolData

abstract class StopLossStrategy(var config: Any, var isVolatile: Boolean) {

    abstract fun calculate(data: SymbolData, signalIndex: Int): Double

    abstract override fun toString(): String

}