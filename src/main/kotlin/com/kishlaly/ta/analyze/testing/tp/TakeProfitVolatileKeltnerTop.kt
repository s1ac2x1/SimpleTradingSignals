package com.kishlaly.ta.analyze.testing.tp

import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.utils.IndicatorUtils

class TakeProfitVolatileKeltnerTop(config: Any?) : TakeProfitStrategy(config = config, isVolatile = true) {
    override fun calculate(data: SymbolData, signalIndex: Int): Double {
        val keltner = IndicatorUtils.buildKeltnerChannels(data.symbol, data.quotes)[signalIndex]
        val keltnerTopRatio = config as Double
        val middle = keltner.middle
        val top = keltner.top
        val diff = top - middle
        val ratio = diff / 100 * keltnerTopRatio
        return middle + ratio
    }

    override fun toString() = "TP volatile ${config as Double}% Keltner top"

}