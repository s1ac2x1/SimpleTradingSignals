package com.kishlaly.ta.analyze.testing.sl

import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.utils.IndicatorUtils

/**
 * SL = Current low – (2 × ATR)
 */
class StopLossVolatileATR : StopLossStrategy(isVolatile = true) {

    override fun calculate(data: SymbolData, signalIndex: Int): Double {
        val signal = data.quotes[signalIndex]
        val atrs = IndicatorUtils.buildATR(data.symbol, data.quotes, 22)
        return signal.low - 2 * atrs[signalIndex].value
    }

    override fun toString() = "SL volatile ATR"

}