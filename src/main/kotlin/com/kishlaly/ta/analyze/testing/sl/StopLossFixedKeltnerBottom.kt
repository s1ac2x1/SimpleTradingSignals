package com.kishlaly.ta.analyze.testing.sl

import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.utils.IndicatorUtils

class StopLossFixedKeltnerBottom : StopLossStrategy() {

    override fun calculate(data: SymbolData, signalIndex: Int): Double {
        return IndicatorUtils.buildKeltnerChannels(data.symbol, data.quotes)[signalIndex].low
    }

    override fun toString() = "SL [Fixed] Keltner 100% bottom"

}