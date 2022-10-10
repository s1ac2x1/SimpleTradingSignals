package com.kishlaly.ta.analyze.tasks.groups.threedisplays

import com.kishlaly.ta.analyze.tasks.ThreeDisplays
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_SoftTrendCheck
import com.kishlaly.ta.analyze.tasks.blocks.two.*
import com.kishlaly.ta.analyze.tasks.groups.AbstractBlocksGroup
import com.kishlaly.ta.analyze.tasks.groups.BlocksGroup

/**
 * First screen: last EMA is rising and the last bar is green
 * Second screen:
 * 1) last two quotes are below EMA13
 * 2) last two quotes rise (low & high rise)
 * 3) last quote is green
 * 4) MACD histogram is negative and last is rising
 * 5) last two %D stochastics are below 20 and the last is rising
 * 6) the last two %K stochastics are below 20 and the last one is rising
 * <p>
 * TP not higher than 50% of the channel
 */
class ThreeDisplays_Buy_8 : AbstractBlocksGroup() {

    override fun init() {
        ThreeDisplays.Config.STOCH_CUSTOM = 20
    }

    override fun blocks(): List<TaskBlock> {
        return object : ArrayList<TaskBlock>() {
            init {
                add(ScreenBasicValidation())

                add(Long_ScreenOne_SoftTrendCheck())

                add(Long_ScreenTwo_EMA_TwoBarsBelow())
                add(Long_ScreenTwo_Bars_TwoAscending())
                add(Long_ScreenTwo_Bars_LastGreen())
                add(Long_ScreenTwo_MACD_TwoBelowZeroAndAscending())
                add(Long_ScreenTwo_Stoch_D_TwoBelow_X())
                add(Long_ScreenTwo_Stoch_D_TwoAscending())
                add(Long_ScreenTwo_Stoch_K_TwoOrOneBelow_X())
                add(Long_ScreenTwo_Stoch_K_TwoAscending())
                add(Long_ScreenTwo_FilterLateEntry())
            }
        }
    }

    override fun comments() = super.comments() + "Finding a strong oversold"

}