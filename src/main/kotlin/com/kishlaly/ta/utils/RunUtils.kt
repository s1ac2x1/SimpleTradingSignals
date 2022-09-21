package com.kishlaly.ta.utils

import com.kishlaly.ta.analyze.TaskType
import com.kishlaly.ta.analyze.tasks.blocks.groups.BlocksGroup
import com.kishlaly.ta.analyze.tasks.blocks.groups.ThreeDisplays_Buy_1
import com.kishlaly.ta.analyze.testing.HistoricalTesting
import com.kishlaly.ta.analyze.testing.TaskTester.Companion.test
import com.kishlaly.ta.analyze.testing.sl.StopLossStrategy
import com.kishlaly.ta.analyze.testing.tp.TakeProfitStrategy
import com.kishlaly.ta.cache.CacheBuilder
import com.kishlaly.ta.cache.CacheBuilder.Companion.getSLStrategies
import com.kishlaly.ta.cache.CacheBuilder.Companion.getTPStrategies
import com.kishlaly.ta.cache.CacheBuilderJava
import com.kishlaly.ta.config.Context
import com.kishlaly.ta.model.Timeframe
import java.util.concurrent.atomic.AtomicInteger

class RunUtils {

    companion object {

        fun buildTasksAndStrategiesSummary_() {
//            buildTasksAndStrategiesSummary(
//                Context.basicTimeframes,
//                TaskType.THREE_DISPLAYS_BUY,
//                listOf(
//
//                )
//            )
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
                        Context.stopLossStrategy = sl
                        Context.takeProfitStrategy = tp
                        println("${current.get().toString()}/${total} ${sl} / ${tp}")
                        blocksGroups.forEach { result.addAll(test(timeframes, task, it)) }
                        current.getAndIncrement()
                    }
                }
            } else {
                Context.stopLossStrategy = stopLossStrategy
                Context.takeProfitStrategy = takeProfitStrategy
                blocksGroups.forEach { result.addAll(test(timeframes, task, it)) }
            }
            CacheBuilder.saveTable(result)
            CacheBuilder.saveSummaryPerGroup(result)
        }

    }

}