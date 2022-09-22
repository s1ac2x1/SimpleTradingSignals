package com.kishlaly.ta.analyze.tasks.blocks.groups

import com.kishlaly.ta.analyze.tasks.Divergencies
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_StrictTrendCheck
import com.kishlaly.ta.analyze.tasks.blocks.two.complex.Long_ScreenTwo_BullishDivergenceMainLogic

class BullishDivergence_Buy_1 : BlocksGroup {

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

    override fun comments() = "Not stable. Pls fix me"

}