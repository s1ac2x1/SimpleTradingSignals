package com.kishlaly.ta.analyze.tasks.groups.threedisplays

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_EMA_LastBarCrosses
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_LastBarGreen
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_LastBarHigher
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_MACD_LastAscending
import com.kishlaly.ta.analyze.tasks.blocks.two.*
import com.kishlaly.ta.analyze.tasks.groups.AbstractBlocksGroup

// modification of buySignalType2 with an attempt to track the beginning of a long-term trend
//
// 1 screen: tracking the beginning of a move above the EMA by two bars
// last bar is green
// last bar is higher than the penultimate one
// last bar crosses EMA26
// last histogram is going up
// 2nd screen: look at the last two bars
// last quote.high is bigger than the penultimate quote.high
// last quote is not above EMA13
// last histogram is going up
// %D and %K of the last stochastic should be higher than the previous
//
// On historical data this strategy most often gives better returns and extremely low SL positions ratio
class ThreeDisplays_Buy_4 : AbstractBlocksGroup() {

    override fun blocks(): List<TaskBlock> {
        return object : ArrayList<TaskBlock>() {
            init {
                add(ScreenBasicValidation())

                add(Long_ScreenOne_LastBarGreen())
                add(Long_ScreenOne_LastBarHigher())
                add(Long_ScreenOne_EMA_LastBarCrosses())
                add(Long_ScreenOne_MACD_LastAscending())

                add(Long_ScreenTwo_Bars_TwoHighAscending())
                add(Long_ScreenTwo_EMA_LastBarNotAbove())
                add(Long_ScreenTwo_MACD_LastAscending())
                add(Long_ScreenTwo_Stoch_D_K_LastAscending())
                add(Long_ScreenTwo_FilterLateEntry())
            }
        }
    }

    override fun comments() = super.comments() + "The best TP/SL and price"

}