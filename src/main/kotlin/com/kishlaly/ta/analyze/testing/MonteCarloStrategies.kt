package com.kishlaly.ta.analyze.testing

import com.kishlaly.ta.analyze.TaskType
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation
import com.kishlaly.ta.analyze.tasks.blocks.one.ScreenOneBlock
import com.kishlaly.ta.analyze.tasks.blocks.two.ScreenTwoBlock
import com.kishlaly.ta.analyze.tasks.groups.BlockGroupsUtils
import com.kishlaly.ta.analyze.tasks.groups.GeneratedBlocksGroup
import com.kishlaly.ta.analyze.testing.sl.StopLossFixedPrice
import com.kishlaly.ta.analyze.testing.tp.TakeProfitFixedKeltnerTop
import com.kishlaly.ta.config.Context
import org.paukov.combinatorics.ICombinatoricsVector
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.stream.StreamSupport

class MonteCarloStrategies(symbol: String, val limit: Long) : MonteCarloBasic(symbol) {

    override fun run() {
        val screenOneGenerator = BlockGroupsUtils().generateBlocksCombinations(
            "com.kishlaly.ta.analyze.tasks.blocks.one",
            clazz = ScreenOneBlock::class.java
        )
        val screenTwoGenerator = BlockGroupsUtils().generateBlocksCombinations(
            "com.kishlaly.ta.analyze.tasks.blocks.two",
            clazz = ScreenTwoBlock::class.java
        )

        val executor = Executors.newFixedThreadPool(10)

        StreamSupport.stream(screenOneGenerator.spliterator(), false)
            .filter { it.size > 0 }
            .limit(limit)
            .forEach { screenOneCombination ->
                StreamSupport.stream(screenTwoGenerator.spliterator(), false)
                    .filter { it.size > 0 }
                    .limit(limit)
                    .forEach { screenTwoCombination ->
                        executor.submit { doTest(screenOneCombination, screenTwoCombination) }
                    }
            }

//        executor.shutdown()
//        executor.awaitTermination(1, TimeUnit.DAYS)
    }

    private fun doTest(
        screenOneCombination: ICombinatoricsVector<ScreenOneBlock>,
        screenTwoCombination: ICombinatoricsVector<ScreenTwoBlock>
    ) {
        println("Testing [${screenOneCombination.vector.size}][${screenTwoCombination.vector.size}]...")
        TaskTester.testOneStrategy(
            Context.basicTimeframes.get(),
            TaskType.THREE_DISPLAYS_BUY,
            GeneratedBlocksGroup(
                listOf(ScreenBasicValidation()),
                screenOneCombination.vector,
                screenTwoCombination.vector
            ),
            StopLossFixedPrice(0.27),
            TakeProfitFixedKeltnerTop(80)
        )
        println("Testing [${screenOneCombination.vector.size}][${screenTwoCombination.vector.size}]... done")
    }

}