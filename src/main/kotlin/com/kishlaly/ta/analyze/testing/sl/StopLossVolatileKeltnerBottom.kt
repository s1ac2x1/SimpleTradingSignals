package com.kishlaly.ta.analyze.testing.sl

import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.utils.IndicatorUtils

class StopLossVolatileKeltnerBottom(config: Any?) : StopLossStrategy(config, true) {

    override fun calculate(data: SymbolData, signalIndex: Int): Double {
        val keltner = IndicatorUtils.buildKeltnerChannels(data.symbol, data.quotes)[signalIndex]
        val bottomRatio = config as Int
        val middle = keltner.middle
        val bottom = keltner.low
        val diff = middle - bottom
        val ratio = diff / 100 * bottomRatio
        return middle - ratio
    }

    override fun toString() = "SL volatile Keltner ${config as Int}% bottom"

}