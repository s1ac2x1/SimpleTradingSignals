package com.kishlaly.ta.analyze.tasks.blocks.groups.bollinger

import com.kishlaly.ta.analyze.tasks.ThreeDisplays
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation
import com.kishlaly.ta.analyze.tasks.blocks.groups.BlocksGroup
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_SoftTrendCheck
import com.kishlaly.ta.analyze.tasks.blocks.two.bars.Long_ScreenTwo_Bars_LastGreen
import com.kishlaly.ta.analyze.tasks.blocks.two.bars.Long_ScreenTwo_Bars_TwoAscending
import com.kishlaly.ta.analyze.tasks.blocks.two.bollinger.Long_ScreenTwo_Bollinger_Bottom_X_Of_Y_LastBarsCrossed
import com.kishlaly.ta.analyze.tasks.blocks.two.keltner.Long_ScreenTwo_FilterLateEntry
import com.kishlaly.ta.analyze.tasks.blocks.two.macd.Long_ScreenTwo_MACD_TwoBelowZeroAndAscending
import com.kishlaly.ta.analyze.tasks.blocks.two.stoch.Long_ScreenTwo_Stoch_D_LastAscending
import com.kishlaly.ta.analyze.tasks.blocks.two.stoch.Long_ScreenTwo_Stoch_D_TwoBelow_X

/**
 * Touching the bottom Bollinger band and a hint of growth.
 * First screen: last EMA is higher and the last bar is green
 * Second screen:
 * + there was a crossing of the lower Bollinger band by one of the last three bars
 * + two MACD histograms below zero and last one is growing
 * + two %D stochastics below 30 and last one is growing
 * + the last bar is green and higher than the previous one
 * + filter late entry
 * <p>
 * SL can be sliding on the average Bollinger band
 */
class ThreeDisplays_Buy_Bollinger_1 : BlocksGroup {

    override fun blocks(): List<TaskBlock> {
        ThreeDisplays.Config.BOLLINGER_TOTAL_BARS_CHECK = 3
        ThreeDisplays.Config.BOLLINGER_CROSSED_BOTTOM_BARS = 1
        ThreeDisplays.Config.STOCH_CUSTOM = 30
        return object : ArrayList<TaskBlock>() {
            init {
                add(ScreenBasicValidation())

                //add(new Long_ScreenOne_StrictTrendCheck());
                add(Long_ScreenOne_SoftTrendCheck())

                add(Long_ScreenTwo_Bollinger_Bottom_X_Of_Y_LastBarsCrossed())
                add(Long_ScreenTwo_MACD_TwoBelowZeroAndAscending())
                add(Long_ScreenTwo_Stoch_D_TwoBelow_X())
                add(Long_ScreenTwo_Stoch_D_LastAscending())
                add(Long_ScreenTwo_Bars_LastGreen())
                add(Long_ScreenTwo_Bars_TwoAscending())
                add(Long_ScreenTwo_FilterLateEntry())
            }
        }
    }

    override fun comments() = "Price recently touched the bottom band, good TP/SL ratio"

}