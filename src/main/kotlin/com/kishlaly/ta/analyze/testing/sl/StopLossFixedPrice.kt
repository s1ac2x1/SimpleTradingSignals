package com.kishlaly.ta.analyze.testing.sl

import com.kishlaly.ta.model.SymbolData

class StopLossFixedPrice(config: Any?) : StopLossStrategy(config = config) {

    companion object {
        var LAST_QUOTES_TO_FIND_MIN = 20
    }

    override fun calculate(data: SymbolData, signalIndex: Int): Double {
        if (data.quotes.size <= LAST_QUOTES_TO_FIND_MIN) {
            return data.quotes[signalIndex].low
        }
        val quoteWithMinimalLow = data.quotes
            .subList(signalIndex - LAST_QUOTES_TO_FIND_MIN, signalIndex)
            .minByOrNull { it.low }
        return quoteWithMinimalLow!!.low - config as Double
    }

    override fun toString() = "SL [Fixed] price ${config as Double}"

}