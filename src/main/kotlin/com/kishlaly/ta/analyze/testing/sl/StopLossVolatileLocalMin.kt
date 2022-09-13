package com.kishlaly.ta.analyze.testing.sl

import com.kishlaly.ta.model.SymbolData

class StopLossVolatileLocalMin(config: Any?) : StopLossStrategy(config, true) {

    companion object {
        var QUOTES_TO_FIND_MIN = 20
    }

    override fun calculate(data: SymbolData, signalIndex: Int): Double {
        val quoteWithMinimalLow = data.quotes.subList(signalIndex - QUOTES_TO_FIND_MIN, signalIndex).minBy { it.low }
        return quoteWithMinimalLow.low - config as Double
    }

    override fun toString() = "SL volatile local min"

}