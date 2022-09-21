package com.kishlaly.ta.utils

import com.kishlaly.ta.model.AbstractModel
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.indicators.Indicator

class SymbolDataUtils<T : AbstractModel>(val symbolData: SymbolData, val clazz: Class<T>) {

    inline fun last(index: Int): T {
        val values = symbolData.indicator(Indicator.findByClass(clazz)) as List<T>
        return values[values.size - index]
    }

}