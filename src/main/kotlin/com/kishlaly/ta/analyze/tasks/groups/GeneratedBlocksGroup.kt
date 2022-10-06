package com.kishlaly.ta.analyze.tasks.groups

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock

class GeneratedBlocksGroup(val screenOneBlocks: List<TaskBlock>, val screenTwoBlocks: List<TaskBlock>) : BlocksGroup {

    override fun blocks(): List<TaskBlock> {
        return screenOneBlocks + screenTwoBlocks
    }

    override fun comments(): String {
        val screenOne = screenOneBlocks.map { it.javaClass.simpleName }.joinToString()
        val screenTwo = screenTwoBlocks.map { it.javaClass.simpleName }.joinToString()
        return "Screen One: \n ${screenOne} \n Screen Two:\n ${screenTwo}"
    }
}