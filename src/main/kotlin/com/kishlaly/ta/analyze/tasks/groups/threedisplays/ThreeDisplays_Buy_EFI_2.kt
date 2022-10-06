package com.kishlaly.ta.analyze.tasks.groups.threedisplays

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation
import com.kishlaly.ta.analyze.tasks.blocks.groups.BlocksGroup

/**
 * The first screen is not used.
 * On the second, three EFI values are checked:
 * + the third and second from the end are negative and rising
 * + the last one is higher and positive
 * <p>
 * SL sliding on the average Bollinger band or TP at the top of the channel, if the last quote is not very high
 * <p>
 * !!! Can't roll over for shorts !!!
 */
//TODO finish
class ThreeDisplays_Buy_EFI_2 : BlocksGroup {

    override fun blocks(): List<TaskBlock> {
        return object : ArrayList<TaskBlock>() {
            init {
                add(ScreenBasicValidation())
            }
        }
    }

    override fun comments() = "EFI rose smoothly and consolidated above zero"

}