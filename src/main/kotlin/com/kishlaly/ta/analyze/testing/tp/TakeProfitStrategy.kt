package com.kishlaly.ta.analyze.testing.tp

import com.kishlaly.ta.model.SymbolData

abstract class TakeProfitStrategy(var config: Any, var isVolatile: Boolean, var enabled: Boolean = true) {

    abstract fun calculate(data: SymbolData, signalIndex: Int)

    abstract override fun toString(): String

}