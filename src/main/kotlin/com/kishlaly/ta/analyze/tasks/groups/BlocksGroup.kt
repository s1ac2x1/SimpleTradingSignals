package com.kishlaly.ta.analyze.tasks.groups

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock

interface BlocksGroup {

    fun blocks(): List<TaskBlock>

    fun comments(): String

}