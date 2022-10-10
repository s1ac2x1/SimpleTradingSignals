package com.kishlaly.ta

import com.kishlaly.ta.analyze.TaskType
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation
import com.kishlaly.ta.analyze.tasks.blocks.one.ScreenOneBlock
import com.kishlaly.ta.analyze.tasks.blocks.two.ScreenTwoBlock
import com.kishlaly.ta.analyze.tasks.groups.BlockGroupsUtils
import com.kishlaly.ta.analyze.tasks.groups.GeneratedBlocksGroup
import com.kishlaly.ta.analyze.testing.TaskTester
import com.kishlaly.ta.analyze.testing.sl.StopLossFixedPrice
import com.kishlaly.ta.analyze.testing.tp.TakeProfitFixedKeltnerTop
import com.kishlaly.ta.cache.CacheReader
import com.kishlaly.ta.config.Context
import com.kishlaly.ta.model.SymbolsSource
import com.kishlaly.ta.model.Timeframe
import com.kishlaly.ta.utils.DBUtils
import com.kishlaly.ta.utils.RunUtils
import java.util.stream.StreamSupport

fun main() {
    Context.aggregationTimeframe = Timeframe.DAY
    Context.source = arrayOf(SymbolsSource.SP500)
    RunUtils.singleSymbol("LUMN")
    Context.symbols = CacheReader.getSymbols()

    DBUtils.initDB()

    val screenOneGenerator = BlockGroupsUtils().generateBlocksCombinations(
        "com.kishlaly.ta.analyze.tasks.blocks.one",
        clazz = ScreenOneBlock::class.java
    )
    val screenTwoGenerator = BlockGroupsUtils().generateBlocksCombinations(
        "com.kishlaly.ta.analyze.tasks.blocks.two",
        clazz = ScreenTwoBlock::class.java
    )

    val limit = 5L
    StreamSupport.stream(screenOneGenerator.spliterator(), false)
        .filter { it.size > 0 }
        .limit(limit)
        .forEach { screenOneCombination ->
            StreamSupport.stream(screenTwoGenerator.spliterator(), false)
                .filter { it.size > 0 }
                .limit(limit)
                .forEach { screenTwoCombination ->
                    println("Testing [${screenOneCombination.vector.size}][${screenTwoCombination.vector.size}]...")
                    TaskTester.testOneStrategy(
                        Context.basicTimeframes,
                        TaskType.THREE_DISPLAYS_BUY,
                        GeneratedBlocksGroup(
                            listOf(ScreenBasicValidation()),
                            screenOneCombination.vector,
                            screenTwoCombination.vector
                        ),
                        StopLossFixedPrice(0.27),
                        TakeProfitFixedKeltnerTop(80)
                    )
                }
        }
}