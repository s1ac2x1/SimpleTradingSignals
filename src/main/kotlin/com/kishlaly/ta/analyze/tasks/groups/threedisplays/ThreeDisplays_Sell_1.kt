package com.kishlaly.ta.analyze.tasks.groups.threedisplays

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation
import com.kishlaly.ta.analyze.tasks.blocks.groups.BlocksGroup
import com.kishlaly.ta.analyze.tasks.blocks.one.Short_ScreenOne_StrictTrendCheck

class ThreeDisplays_Sell_1 : BlocksGroup {

    override fun blocks(): List<TaskBlock> {
        return object : ArrayList<TaskBlock>() {
            init {
                add(ScreenBasicValidation())
                add(Short_ScreenOne_StrictTrendCheck())
//                add(Short_ScreenTwo_MACD_ThreeAboveZeroAndDescending())
//                add(Short_ScreenTwo_Stoch_D_ThreeDescending())
//                add(Short_ScreenTwo_Stoch_D_K_ThreeDescendingFromOverbought())
//                add(Short_ScreenTwo_EMA_ThreeBarsDescendingAndCrossing())
//                add(Short_ScreenTwo_EMA_LastBarTooLow())
            }
        }
    }

    override fun comments() = "The first short strategy. Not stable"

}