package com.kishlaly.ta.analyze.tasks.groups.threedisplays

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenSoftValidation
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_Bars_LastGreen
import com.kishlaly.ta.analyze.tasks.groups.AbstractBlocksGroup

/**
 * Second screen:
 * + Intersection of the bottom Bollinger band (the color of the bar is not important)
 * <p>
 * entry 7 cents above the last bar
 * or TP at the middle of the channel
 */
class ThreeDisplays_Buy_Bollinger_4 : AbstractBlocksGroup() {

    override fun blocks(): List<TaskBlock> {
        return object : ArrayList<TaskBlock>() {
            init {
                add(ScreenBasicValidation())
                add(ScreenSoftValidation())

                add(Long_ScreenTwo_Bars_LastGreen())
            }
        }
    }

    override fun comments() = super.comments() + "Observe: the price has crossed the bottom band"

}