package com.kishlaly.ta.analyze.tasks.groups.threedisplays

import com.kishlaly.ta.analyze.tasks.ThreeDisplays
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_SoftTrendCheck
import com.kishlaly.ta.analyze.tasks.blocks.two.*
import com.kishlaly.ta.analyze.tasks.groups.AbstractBlocksGroup

// inspired by [D] CFLT 20 Dec 2021
//
// first screen: last EMA is risign and the last bar is green
// second screen:
// oversold below 20 at TWO %D stochastic values
// last histogram is rising
// last bar is green
// last two bars go up (quote.low & quote.high)
// last two bars fully below EMA13
//
// entry 7 cents above the close of the last bar
// TP in the middle of the upper half of the Keltner channel
class ThreeDisplays_Buy_3 : AbstractBlocksGroup() {

    override fun init() {
        ThreeDisplays.Config.BOLLINGER_TOTAL_BARS_CHECK = 3
        ThreeDisplays.Config.BOLLINGER_CROSSED_BOTTOM_BARS = 1
    }

    override fun blocks(): List<TaskBlock> {
        return object : ArrayList<TaskBlock>() {
            init {
                add(ScreenBasicValidation())
                add(Long_ScreenOne_SoftTrendCheck())

                add(Long_ScreenTwo_Stoch_D_TwoStrongOversold())
                add(Long_ScreenTwo_MACD_LastAscending())
                add(Long_ScreenTwo_Bars_LastGreen())
                add(Long_ScreenTwo_Bars_TwoAscending())
                add(Long_ScreenTwo_EMA_TwoBarsBelow())
                add(Long_ScreenTwo_FilterLateEntry())
            }
        }
    }

    override fun comments() = super.comments() + "Often good results, but the average SL is high"

}