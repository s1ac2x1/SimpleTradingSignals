package com.kishlaly.ta.analyze.tasks.blocks.groups

import com.kishlaly.ta.analyze.tasks.ThreeDisplays
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_EMA_LastBarCrosses
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_EMA_X_OutOf_Y_Above
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_LastBarGreen
import com.kishlaly.ta.analyze.tasks.blocks.two.macd.Long_ScreenTwo_MACD_TwoBelowZeroAndAscending

/**
 * Price returns to the EMA on an uptrend
 * <p>
 * First screen
 * 1) last bar is green
 * 2) last bar crosses the EMA26
 * 3) out of last 7 bars at least 4 are completely above EMA26
 * <p>
 * SL: try StopLossVolatileLocalMin
 */
class FirstScreen_Buy_1 : BlocksGroup {

    override fun blocks(): List<TaskBlock> {
        ThreeDisplays.Config.EMA26_TOTAL_BARS_CHECK = 7
        ThreeDisplays.Config.EMA26_ABOVE_BARS = 4

        return object : ArrayList<TaskBlock>() {
            init {
                add(ScreenBasicValidation())
                add(Long_ScreenOne_LastBarGreen())
                add(Long_ScreenOne_EMA_LastBarCrosses())
                add(Long_ScreenOne_EMA_X_OutOf_Y_Above())
                add(Long_ScreenTwo_MACD_TwoBelowZeroAndAscending())
            }
        }
    }

    override fun comments() = "Looking for the return of the price to EMA26 on the uptrend of the first screen"
}