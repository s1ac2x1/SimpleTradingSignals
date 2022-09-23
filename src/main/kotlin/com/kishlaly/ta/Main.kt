package com.kishlaly.ta

import com.kishlaly.ta.analyze.TaskType
import com.kishlaly.ta.analyze.testing.TaskTester
import com.kishlaly.ta.analyze.testing.sl.StopLossFixedPrice
import com.kishlaly.ta.analyze.testing.tp.TakeProfitFixedKeltnerTop
import com.kishlaly.ta.cache.CacheReader
import com.kishlaly.ta.config.Context
import com.kishlaly.ta.model.SymbolsSource
import com.kishlaly.ta.model.Timeframe
import com.kishlaly.ta.utils.Context
import com.kishlaly.ta.utils.RunUtils

fun main() {
    Context.aggregationTimeframe = Timeframe.DAY
    Context.source = arrayOf(SymbolsSource.SP500)
    RunUtils.singleSymbol("AAPL")

    Context.symbols = CacheReader.getSymbols()
    //buildCache(Context.basicTimeframes, false);
    //buildCache(Context.basicTimeframes, false);
    TaskTester.testOneStrategy(
        Context.basicTimeframes, TaskType.THREE_DISPLAYS_BUY,
        strategy,
        StopLossFixedPrice(0.27),
        TakeProfitFixedKeltnerTop(80)
    )

}