package com.kishlaly.ta.analyze.tasks.groups.threedisplays

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_SoftTrendCheck
import com.kishlaly.ta.analyze.tasks.blocks.two.*
import com.kishlaly.ta.analyze.tasks.groups.AbstractBlocksGroup

class ThreeDisplays_Buy_2 : AbstractBlocksGroup() {

    override fun blocks(): List<TaskBlock> {
        return object : ArrayList<TaskBlock>() {
            init {
                add(ScreenBasicValidation())
                add(Long_ScreenOne_SoftTrendCheck())

                add(Long_ScreenTwo_MACD_TwoBelowZeroAndAscending())
                add(Long_ScreenTwo_Stoch_D_K_SomeWereOversold())
                add(Long_ScreenTwo_Stoch_D_LastAscending())
                add(Long_ScreenTwo_EMA_TwoBarsAscendingAndCrossing())
                add(Long_ScreenTwo_FilterLateEntry())
            }
        }
    }

    override fun comments() = super.comments() + "In second place in terms of efficiency"

}