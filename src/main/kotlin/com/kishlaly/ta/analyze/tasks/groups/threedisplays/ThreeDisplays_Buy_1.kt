package com.kishlaly.ta.analyze.tasks.groups.threedisplays

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation
import com.kishlaly.ta.analyze.tasks.blocks.groups.BlocksGroup
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_SoftTrendCheck
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_EMA_LastBarTooHigh
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_EMA_ThreeBarsAscendingAndCrossing
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_FilterLateEntry
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_MACD_ThreeBelowZeroAndAscending
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_Stoch_D_K_ThreeAscendingFromOversold
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_Stoch_D_ThreeAscending

class ThreeDisplays_Buy_1 : BlocksGroup {

    override fun blocks(): List<TaskBlock> {
        return object : ArrayList<TaskBlock>() {
            init {
                add(ScreenBasicValidation())
                add(Long_ScreenOne_SoftTrendCheck())

                add(Long_ScreenTwo_MACD_ThreeBelowZeroAndAscending())
                add(Long_ScreenTwo_Stoch_D_ThreeAscending())
                add(Long_ScreenTwo_Stoch_D_K_ThreeAscendingFromOversold())
                add(Long_ScreenTwo_EMA_ThreeBarsAscendingAndCrossing())
                add(Long_ScreenTwo_EMA_LastBarTooHigh())
                add(Long_ScreenTwo_FilterLateEntry())
            }
        }
    }

    override fun comments() = "Often good results, low SL ratio"
}