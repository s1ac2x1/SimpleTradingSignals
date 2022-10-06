package com.kishlaly.ta.analyze.tasks.groups.threedisplays

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_SoftTrendCheck
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_Bollinger_Bottom_LastBarCrossed
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_Bollinger_Bottom_PreLastBelow
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_MACD_TwoBelowZeroAndAscending
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_Stoch_D_LastAscending
import com.kishlaly.ta.analyze.tasks.groups.BlocksGroup

/**
 * A rare occurrence when the price goes down beyond the Bollinger band
 * <p>
 * First screen: last EMA is higher and the last bar is green
 * Second screen:
 * + prelast quote is below bottom Bollinger band
 * + last quote crosses the bottom Bollinger band
 * + two last MACD histograms are negative and the last one is higher
 * + Last %D stochastic is rising
 */
class ThreeDisplays_Buy_Bollinger_2 : BlocksGroup {

    override fun blocks(): List<TaskBlock> {
        return object : ArrayList<TaskBlock>() {
            init {
                add(ScreenBasicValidation())

                add(Long_ScreenOne_SoftTrendCheck())

                add(Long_ScreenTwo_Bollinger_Bottom_PreLastBelow())
                add(Long_ScreenTwo_Bollinger_Bottom_LastBarCrossed())
                add(Long_ScreenTwo_MACD_TwoBelowZeroAndAscending())
                add(Long_ScreenTwo_Stoch_D_LastAscending())
            }
        }
    }

    override fun comments() = "Finding the fallout behind the bottom band. Doesn't work well."

}