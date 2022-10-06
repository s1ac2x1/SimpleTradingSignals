package com.kishlaly.ta.analyze.tasks.groups.threedisplays

import com.kishlaly.ta.analyze.tasks.ThreeDisplays
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_SoftTrendCheck
import com.kishlaly.ta.analyze.tasks.blocks.two.*
import com.kishlaly.ta.analyze.tasks.groups.BlocksGroup
import com.kishlaly.ta.analyze.testing.sl.StopLossFixedPrice

/**
 * First screen: last EMA is rising and the last quote is green
 * Second screen:
 * 1) the last MACD histogram is rising
 * 2) one of two stochastic %D values is below 20
 * 3) last two quotes are below EMA13
 * 4) Last quote is green
 * <p>
 * TP is not higher than 50% of the distance from the middle to the top of the Keltner channel
 */
class ThreeDisplays_Buy_6 : BlocksGroup {

    override fun blocks(): List<TaskBlock> {
        ThreeDisplays.Config.STOCH_CUSTOM = 20
        StopLossFixedPrice.LAST_QUOTES_TO_FIND_MIN = 40
        return object : ArrayList<TaskBlock>() {
            init {
                add(ScreenBasicValidation())

                add(Long_ScreenOne_SoftTrendCheck())

                add(Long_ScreenTwo_MACD_LastAscending())
                add(Long_ScreenTwo_Stoch_D_TwoOrOneBelow_X())
                add(Long_ScreenTwo_EMA_TwoBarsBelow())
                add(Long_ScreenTwo_Bars_LastGreen())
                add(Long_ScreenTwo_FilterLateEntry())
            }
        }
    }

    override fun comments() =
        "A variation on the search for a rise out of oversold. Good returns and number of positions"

}