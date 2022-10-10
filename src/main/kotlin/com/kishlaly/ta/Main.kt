package com.kishlaly.ta

import com.kishlaly.ta.analyze.TaskType
import com.kishlaly.ta.analyze.tasks.groups.threedisplays.ThreeDisplays_Buy_4
import com.kishlaly.ta.analyze.tasks.groups.threedisplays.ThreeDisplays_Buy_5
import com.kishlaly.ta.analyze.tasks.groups.threedisplays.ThreeDisplays_Buy_7
import com.kishlaly.ta.analyze.testing.MonteCarloStrategies
import com.kishlaly.ta.analyze.testing.TaskTester
import com.kishlaly.ta.analyze.testing.sl.StopLossFixedPrice
import com.kishlaly.ta.analyze.testing.tp.TakeProfitFixedKeltnerTop
import com.kishlaly.ta.cache.CacheReader
import com.kishlaly.ta.config.Context
import com.kishlaly.ta.model.SymbolsSource
import com.kishlaly.ta.model.Timeframe
import com.kishlaly.ta.utils.DBUtils
import com.kishlaly.ta.utils.RunUtils

fun main() {
//    Context.aggregationTimeframe = Timeframe.DAY
//    Context.source = arrayOf(SymbolsSource.SP500)
//    RunUtils.singleSymbol("LUMN")
//
//    Context.symbols = CacheReader.getSymbols()
//    //buildCache(Context.basicTimeframes, false);

//    DBUtils.initDB()
//    TaskTester.testOneStrategy(
//        Context.basicTimeframes,
//        TaskType.THREE_DISPLAYS_BUY,
//        ThreeDisplays_Buy_4(),
//        StopLossFixedPrice(0.27),
//        TakeProfitFixedKeltnerTop(95)
//    )

//    RunUtils.buildTasksAndStrategiesSummary_()

// ====================

    MonteCarloStrategies("LUMN", 50).run()

}