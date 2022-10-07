package com.kishlaly.ta.analyze.tasks.groups.trustmodel

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenSoftValidation
import com.kishlaly.ta.analyze.tasks.blocks.complex.Long_ScreenTwo_FirstTrustModelMainLogic
import com.kishlaly.ta.analyze.tasks.groups.AbstractBlocksGroup
import com.kishlaly.ta.config.Context

class FirstTrustModel_Buy_1 : AbstractBlocksGroup() {
    override fun blocks(): List<TaskBlock> {
        Context.TRIM_DATA = false

        return object : ArrayList<TaskBlock>() {
            init {
                add(ScreenSoftValidation())
                add(Long_ScreenTwo_FirstTrustModelMainLogic())
            }
        }

    }

    override fun comments() = super.comments() + "The First Trust Model"
}