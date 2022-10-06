package com.kishlaly.ta.analyze.tasks.groups.threedisplays

import com.kishlaly.ta.analyze.tasks.ThreeDisplays
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation
import com.kishlaly.ta.analyze.tasks.blocks.groups.BlocksGroup
import com.kishlaly.ta.analyze.tasks.blocks.two.*
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_Bollinger_Bottom_ThreeAscending
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_Bollinger_Top_ThreeDescending
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_MACD_ThreeAscending
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_Stoch_D_TwoAscending
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_Stoch_D_TwoOrOneBelow_X

/**
 * Search for narrowing bands with signs of bullish growth
 * <p>
 * Second screen:
 * + the last three values of the upper band are declining -- maybe check the last 4-5 values? TODO
 * + the last three values of the lower band are rising -- maybe check the last 4-5 values? TODO
 * + the last three MACD histograms are rising (not necessarily all three below zero!)
 * + last two %D of stochastics are going up
 * + penultimate %D stochastic is below 40
 * <p>
 * SL sliding in the middle of the Keltner channel
 * <p>
 * Example: [D] AAPL 15.11.2021, 7.06.2021
 */
class ThreeDisplays_Buy_Bollinger_3 : BlocksGroup {

    override fun blocks(): List<TaskBlock> {
        ThreeDisplays.Config.STOCH_CUSTOM = 40
        return object : ArrayList<TaskBlock>() {
            init {
                add(ScreenBasicValidation())

                add(Long_ScreenTwo_Bollinger_Top_ThreeDescending())
                add(Long_ScreenTwo_Bollinger_Bottom_ThreeAscending())
                add(Long_ScreenTwo_MACD_ThreeAscending())
                add(Long_ScreenTwo_Stoch_D_TwoAscending())
                add(Long_ScreenTwo_Stoch_D_TwoOrOneBelow_X())
            }
        }
    }

    override fun comments() = "Narrowing of the Bollinger Bands with signs of bullish growth"

}