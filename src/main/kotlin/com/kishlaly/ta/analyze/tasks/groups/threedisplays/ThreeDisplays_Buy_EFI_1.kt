package com.kishlaly.ta.analyze.tasks.groups.threedisplays

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_EFI_ThirdNegative
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_EFI_TwoPositive
import com.kishlaly.ta.analyze.tasks.groups.AbstractBlocksGroup

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
class ThreeDisplays_Buy_EFI_1 : AbstractBlocksGroup() {

    override fun blocks(): List<TaskBlock> {
        return object : ArrayList<TaskBlock>() {
            init {
                add(ScreenBasicValidation())

                add(Long_ScreenTwo_EFI_ThirdNegative())
                add(Long_ScreenTwo_EFI_TwoPositive())
            }
        }
    }

    override fun comments() = super.comments() + "EFI rose above zero and two values are positive"

}