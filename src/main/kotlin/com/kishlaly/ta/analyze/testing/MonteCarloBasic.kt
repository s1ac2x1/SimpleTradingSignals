package com.kishlaly.ta.analyze.testing

import com.kishlaly.ta.cache.CacheReader
import com.kishlaly.ta.config.Context
import com.kishlaly.ta.model.SymbolsSource
import com.kishlaly.ta.model.Timeframe
import com.kishlaly.ta.utils.DBUtils
import com.kishlaly.ta.utils.RunUtils

abstract class MonteCarloBasic {

    constructor(symbol: String) {
        Context.aggregationTimeframe.set(Timeframe.DAY)
        Context.source = arrayOf(SymbolsSource.SP500)
        RunUtils.singleSymbol(symbol)
        Context.symbols.set(CacheReader.getSymbols())
        DBUtils.initDB()
    }

    abstract fun run()

}