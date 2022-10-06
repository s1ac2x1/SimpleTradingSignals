package com.kishlaly.ta

import com.kishlaly.ta.analyze.tasks.blocks.one.ScreenOneBlock
import org.paukov.combinatorics.CombinatoricsFactory
import org.reflections.Reflections

fun main() {
    var screenOneBlocks = generateScreenOneBlocksSet("com.kishlaly.ta.analyze.tasks.blocks.one")
    println(screenOneBlocks.size)

}

private fun generateScreenOneBlocksSet(pckg: String, direction: String = "Long"): List<List<ScreenOneBlock>> {
    var blocks = Reflections(pckg)
        .getSubTypesOf(ScreenOneBlock::class.java)
        .filter { it.simpleName.contains(direction) }
        .map { it.constructors.first().newInstance() as ScreenOneBlock }
        .toList()

    for (block in blocks) {
        println(block.javaClass.simpleName)
    }

    val vector = CombinatoricsFactory.createVector(blocks)
    val generator = CombinatoricsFactory.createSubSetGenerator(vector)
    val result = mutableListOf<List<ScreenOneBlock>>()
    for (combination in generator) {
        result.add(combination.vector)
    }
    return result.toList()
}
