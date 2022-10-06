package com.kishlaly.ta.analyze.tasks.blocks.groups.threedisplays

import com.kishlaly.ta.analyze.tasks.ThreeDisplays
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation
import com.kishlaly.ta.analyze.tasks.blocks.groups.BlocksGroup
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_SoftTrendCheck
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_Bars_TwoHighAscending
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_EMA_LastBarNotAbove
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_FilterLateEntry
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_MACD_LastAscending
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_Stoch_D_K_LastAscending

// ThreeDisplays_Buy_4 modification to search for ultra-short positions
// 1 screen: last EMA is higher and the last bar is green
// 2 screen: added check that the last quote does not go higher than 10% from the middle of the channel
//
// TP 50-70% of the channel
//
// on historical tests shows a good balance, but the number of SL positions is much higher than the TP
class ThreeDisplays_Buy_7 : BlocksGroup {

    override fun blocks(): List<TaskBlock> {
        ThreeDisplays.Config.FILTER_BY_KELTNER_ENABLED = true
        ThreeDisplays.Config.FILTER_BY_KELTNER = 10
        return object : ArrayList<TaskBlock>() {
            init {
                add(ScreenBasicValidation())

                add(Long_ScreenOne_SoftTrendCheck())

                add(Long_ScreenTwo_Bars_TwoHighAscending())
                add(Long_ScreenTwo_EMA_LastBarNotAbove())
                add(Long_ScreenTwo_MACD_LastAscending())
                add(Long_ScreenTwo_Stoch_D_K_LastAscending())
                add(Long_ScreenTwo_FilterLateEntry())
            }
        }
    }

    override fun comments() = "Good returns, but a lot of positions"

}