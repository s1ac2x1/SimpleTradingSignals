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
import com.kishlaly.ta.config.Context
import com.kishlaly.ta.utils.DBUtils

fun main() {
    DBUtils.initDB()

    val screenOneGenerator = BlockGroupsUtils().generateBlocksCombinations(
        "com.kishlaly.ta.analyze.tasks.blocks.one",
        clazz = ScreenOneBlock::class.java
    )
    val screenTwoGenerator = BlockGroupsUtils().generateBlocksCombinations(
        "com.kishlaly.ta.analyze.tasks.blocks.two",
        clazz = ScreenTwoBlock::class.java
    )
    var i = 0
    outer@ for (screenOneCombination in screenOneGenerator) {
        for (screenTwoCombination in screenTwoGenerator) {
            if (screenOneCombination.vector.size > 0 && screenTwoCombination.vector.size > 0) {
                println("Testing blocks with [${screenOneCombination.size}][${screenTwoCombination.size}]...")
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
                i++
                if (i > 5) {
                    break@outer
                }
            } else {
                println("Skipped [${screenOneCombination.size}][${screenTwoCombination.size}]")
            }
        }
    }
}