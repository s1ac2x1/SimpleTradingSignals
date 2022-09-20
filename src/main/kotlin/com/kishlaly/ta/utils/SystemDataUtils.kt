package com.kishlaly.ta.utils

import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.indicators.Indicator
import com.kishlaly.ta.model.indicators.MACD

class MACDUtils(val symbolData: SymbolData) {

    fun getFromEnd(index: Int): MACD {
        val values = symbolData.indicator(Indicator.MACD) as List<MACD>
        return values[values.size - index]
    }

}