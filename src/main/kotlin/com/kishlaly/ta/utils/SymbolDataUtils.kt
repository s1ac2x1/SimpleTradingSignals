package com.kishlaly.ta.utils

import com.kishlaly.ta.model.AbstractModel
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.indicators.Indicator

class SymbolDataUtils<T : AbstractModel>(val symbolData: SymbolData, val clazz: Class<T>) {

    fun last(fromEnd: Int = 1): T {
        val values = fetchValues()
        return values[values.size - fromEnd]
    }

    operator fun get(index: Int) = fetchValues()[index]

    fun size() = fetchValues().size

    private fun fetchValues() = symbolData.indicator(Indicator.findByClass(clazz)) as List<T>

}