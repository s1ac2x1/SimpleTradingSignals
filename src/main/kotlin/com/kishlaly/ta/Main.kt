package com.kishlaly.ta

import com.kishlaly.ta.analyze.tasks.blocks.groups.ThreeDisplays_Buy_4Java
import com.kishlaly.ta.cache.CacheReaderJava
import com.kishlaly.ta.config.Context
import com.kishlaly.ta.model.SymbolsSource
import com.kishlaly.ta.model.Timeframe
import com.kishlaly.ta.utils.ContextJava
import com.kishlaly.ta.utils.RunUtilsJava

fun main() {
    Context.aggregationTimeframe = Timeframe.DAY
    Context.source = arrayOf(SymbolsSource.SP500)
    RunUtilsJava.singleSymbol("AAPL") // for single test

    ContextJava.symbols = CacheReaderJava.getSymbols()
    //buildCache(Context.basicTimeframes, false);
    //buildCache(Context.basicTimeframes, false);
    RunUtilsJava.testOneStrategy_(ThreeDisplays_Buy_4Java())

}