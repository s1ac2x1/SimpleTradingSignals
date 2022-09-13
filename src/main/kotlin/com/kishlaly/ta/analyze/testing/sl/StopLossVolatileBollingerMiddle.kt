package com.kishlaly.ta.analyze.testing.sl

import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.utils.IndicatorUtils

class StopLossVolatileBollingerMiddle : StopLossStrategy(isVolatile = true) {

    override fun calculate(data: SymbolData, signalIndex: Int): Double {
        return IndicatorUtils.buildBollingerBands(data.symbol, data.quotes)[signalIndex].middle
    }

    override fun toString() = "SL volatile Bollinger middle"
}