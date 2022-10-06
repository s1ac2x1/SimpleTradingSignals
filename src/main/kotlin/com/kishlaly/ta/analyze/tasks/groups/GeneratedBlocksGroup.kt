package com.kishlaly.ta.analyze.tasks.groups

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock

class GeneratedBlocksGroup(
    val commonBlocks: List<TaskBlock>,
    val screenOneBlocks: List<TaskBlock>,
    val screenTwoBlocks: List<TaskBlock>
) : BlocksGroup {

    override fun blocks(): List<TaskBlock> {
        return commonBlocks + screenOneBlocks + screenTwoBlocks
    }

    override fun comments(): String {
        val common = commonBlocks.map { it.javaClass.simpleName }.joinToString()
        val screenOne = screenOneBlocks.map { it.javaClass.simpleName }.joinToString()
        val screenTwo = screenTwoBlocks.map { it.javaClass.simpleName }.joinToString()
        return "Screen One: \n ${screenOne} \n\n Screen Two:\n ${screenTwo} \n\n Common: \n${common}"
    }
}