package com.kishlaly.ta.analyze.tasks.groups.threedisplays

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation
import com.kishlaly.ta.analyze.tasks.groups.BlocksGroup

/**
 * Second screen:
 * + Intersection of the bottom Bollinger band (the color of the bar is not important)
 * <p>
 * entry 7 cents above the last bar
 * or TP at the middle of the channel
 */
//TODO finish
class ThreeDisplays_Buy_Bollinger_4 : BlocksGroup {

    override fun blocks(): List<TaskBlock> {
        return object : ArrayList<TaskBlock>() {
            init {
                add(ScreenBasicValidation())
            }
        }
    }

    override fun comments() = "Observe: the price has crossed the bottom band"

}