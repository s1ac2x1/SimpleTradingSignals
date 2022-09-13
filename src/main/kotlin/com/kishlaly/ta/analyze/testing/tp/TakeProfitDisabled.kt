package com.kishlaly.ta.analyze.testing.tp

import com.kishlaly.ta.model.SymbolData

class TakeProfitDisabled : TakeProfitStrategy() {

    override fun calculate(data: SymbolData, signalIndex: Int) = -1.0

    override fun toString() = "TP disabled"

}