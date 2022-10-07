package com.kishlaly.ta.analyze.tasks.groups.divergencies

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation
import com.kishlaly.ta.analyze.tasks.blocks.complex.Long_ScreenTwo_BullishDivergenceMainLogic
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_StrictTrendCheck
import com.kishlaly.ta.analyze.tasks.groups.AbstractBlocksGroup

class BullishDivergence_Buy_1 : AbstractBlocksGroup() {

    override fun blocks(): List<TaskBlock> {
        return object : ArrayList<TaskBlock>() {
            init {
                add(ScreenBasicValidation())
                if (!Divergencies.BullishConfig.ALLOW_ON_BEARISH_TREND) {
                    add(Long_ScreenOne_StrictTrendCheck())
                }

                add(Long_ScreenTwo_BullishDivergenceMainLogic())
            }
        }
    }

    override fun comments() = super.comments() + "Not stable. Pls fix me"

}