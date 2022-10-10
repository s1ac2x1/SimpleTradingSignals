package com.kishlaly.ta.utils

import com.kishlaly.ta.analyze.TaskType
import com.kishlaly.ta.analyze.tasks.groups.BlocksGroup
import com.kishlaly.ta.analyze.tasks.groups.threedisplays.*
import com.kishlaly.ta.analyze.testing.HistoricalTesting
import com.kishlaly.ta.analyze.testing.TaskTester.Companion.test
import com.kishlaly.ta.analyze.testing.sl.StopLossFixedPrice
import com.kishlaly.ta.analyze.testing.sl.StopLossStrategy
import com.kishlaly.ta.analyze.testing.tp.TakeProfitFixedKeltnerTop
import com.kishlaly.ta.analyze.testing.tp.TakeProfitStrategy
import com.kishlaly.ta.cache.CacheBuilder
import com.kishlaly.ta.cache.CacheBuilder.Companion.getSLStrategies
import com.kishlaly.ta.cache.CacheBuilder.Companion.getTPStrategies
import com.kishlaly.ta.config.Context
import com.kishlaly.ta.model.Timeframe
import java.util.concurrent.atomic.AtomicInteger

class RunUtils {

    companion object {

        fun buildTasksAndStrategiesSummary_() {
            buildTasksAndStrategiesSummary(
                Context.basicTimeframes.get(),
                TaskType.THREE_DISPLAYS_BUY,
                listOf(
                    ThreeDisplays_Buy_1(),
                    ThreeDisplays_Buy_2(),
                    ThreeDisplays_Buy_3(),
                    ThreeDisplays_Buy_4(),
                    ThreeDisplays_Buy_5(),
                    ThreeDisplays_Buy_6(),
                    ThreeDisplays_Buy_7(),
                    ThreeDisplays_Buy_8(),
                    ThreeDisplays_Buy_9(),
                    ThreeDisplays_Buy_Bollinger_1(),
                    ThreeDisplays_Buy_Bollinger_1_2(),
                    ThreeDisplays_Buy_Bollinger_2(),
                    ThreeDisplays_Buy_Bollinger_3(),
                    ThreeDisplays_Buy_Bollinger_4(),
//                    ThreeDisplays_Buy_EFI_1(),
//                    ThreeDisplays_Buy_EFI_2(),
//                    ThreeDisplays_Buy_EFI_3(),
//                    Experiments()
                ),
                StopLossFixedPrice(0.27),
                TakeProfitFixedKeltnerTop(80)
            )
        }

        fun buildTasksAndStrategiesSummary(
            timeframes: Array<Array<Timeframe>>,
            task: TaskType,
            blocksGroups: List<BlocksGroup>,
            stopLossStrategy: StopLossStrategy,
            takeProfitStrategy: TakeProfitStrategy
        ) {
            val result = mutableListOf<HistoricalTesting>()
            val total = getSLStrategies().size * getTPStrategies().size
            val current = AtomicInteger(1)
            if (stopLossStrategy == null || takeProfitStrategy == null) {
                getSLStrategies().forEach { sl ->
                    getTPStrategies().forEach { tp ->
                        Context.stopLossStrategy.set(sl)
                        Context.takeProfitStrategy.set(tp)
                        println("${current.get().toString()}/${total} ${sl} / ${tp}")
                        blocksGroups.forEach { result.addAll(test(timeframes, task, it)) }
                        current.getAndIncrement()
                    }
                }
            } else {
                Context.stopLossStrategy.set(stopLossStrategy)
                Context.takeProfitStrategy.set(takeProfitStrategy)
                blocksGroups.forEach { result.addAll(test(timeframes, task, it)) }
            }
            CacheBuilder.saveTable(result)
            CacheBuilder.saveSummaryPerGroup(result)
        }

        fun singleSymbol(symbol: String) {
            Context.testOnly = mutableListOf(symbol)
        }

    }

}