package com.kishlaly.ta.analyze.tasks.groups.threedisplays

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation
import com.kishlaly.ta.analyze.tasks.groups.BlocksGroup

/**
 * The first screen is not used
 * The second screen checks the three EFI values:
 * + third from the end is negative
 * + penultimate and last are positive
 * <p>
 * SL sliding on the average Bollinger band or TP at the top of the channel, if the last quote is not very high
 * <p>
 * !!! Can't flip for shorts !!!
 */
//TODO finish
class ThreeDisplays_Buy_EFI_1 : BlocksGroup {

    override fun blocks(): List<TaskBlock> {
        return object : ArrayList<TaskBlock>() {
            init {
                add(ScreenBasicValidation())
            }
        }
    }

    override fun comments() = "EFI rose above zero and two values are positive"

}