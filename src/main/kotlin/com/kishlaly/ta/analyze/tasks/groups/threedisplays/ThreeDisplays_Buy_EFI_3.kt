package com.kishlaly.ta.analyze.tasks.groups.threedisplays

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation
import com.kishlaly.ta.analyze.tasks.groups.AbstractBlocksGroup

/**
 * The first screen is not used.
 * The second one checks the three EFI values:
 * + all three last values are negative
 * + the second from the end value is lower than the third from the end (the lower point of the figure U)
 * + the last value is higher than the second from the end (the right point of the figure U)
 * <p>
 * SL sliding on the average Bollinger band or TP at the top of the channel, if the last quote is not very high
 * <p>
 * Note: Can't flip for shorts
 */
//TODO implement
class ThreeDisplays_Buy_EFI_3 : AbstractBlocksGroup() {

    override fun blocks(): List<TaskBlock> {
        return object : ArrayList<TaskBlock>() {
            init {
                add(ScreenBasicValidation())
            }
        }
    }

    override fun comments() = super.comments() + "EFI draws U below zero"

}