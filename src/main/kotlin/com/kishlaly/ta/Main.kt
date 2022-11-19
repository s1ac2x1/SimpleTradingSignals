package com.kishlaly.ta

import com.kishlaly.ta.analyze.TaskType
import com.kishlaly.ta.analyze.tasks.groups.Experiments
import com.kishlaly.ta.analyze.tasks.groups.threedisplays.ThreeDisplays_Buy_4
import com.kishlaly.ta.analyze.tasks.groups.threedisplays.ThreeDisplays_Buy_EFI_2
import com.kishlaly.ta.analyze.testing.TaskRunner
import com.kishlaly.ta.analyze.testing.TaskTester
import com.kishlaly.ta.analyze.testing.sl.StopLossFixedPrice
import com.kishlaly.ta.analyze.testing.sl.StopLossVolatileKeltnerBottom
import com.kishlaly.ta.analyze.testing.tp.TakeProfitFixedKeltnerTop
import com.kishlaly.ta.cache.CacheBuilder.Companion.buildCache
import com.kishlaly.ta.cache.CacheReader
import com.kishlaly.ta.config.Context
import com.kishlaly.ta.model.SymbolsSource
import com.kishlaly.ta.model.Timeframe
import com.kishlaly.ta.utils.RunUtils

fun main() {
    Context.aggregationTimeframe.set(Timeframe.DAY)
    Context.source = arrayOf(SymbolsSource.TEST)
//    RunUtils.singleSymbol("LUMN")

    Context.symbols.set(CacheReader.getSymbols())
//    buildCache(Context.basicTimeframes.get(), false);

    TaskRunner.run(blocksGroups = arrayOf(Experiments()))

//    DBUtils.initDB()
//    TaskTester.testOneStrategy(
//        Context.basicTimeframes.get(),
//        TaskType.THREE_DISPLAYS_BUY,
//        Experiments()
//    )

//    RunUtils.buildTasksAndStrategiesSummary_()

// ====================

//    MonteCarloStrategies("LUMN", 50).run()

}