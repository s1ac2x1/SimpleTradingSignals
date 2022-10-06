package com.kishlaly.ta

import com.kishlaly.ta.analyze.tasks.blocks.one.ScreenOneBlock
import org.paukov.combinatorics.CombinatoricsFactory
import org.reflections.Reflections

fun main() {
    val screenOneBlocks =
        generateBlocksCombinations<ScreenOneBlock>(
            "com.kishlaly.ta.analyze.tasks.blocks.one",
            clazz = ScreenOneBlock::class.java
        )
    println(screenOneBlocks.size)
}

fun <T> generateBlocksCombinations(pckg: String, direction: String = "Long", clazz: Class<T>): List<List<T>> {
    var blocks = Reflections(pckg)
        .getSubTypesOf(clazz)
        .filter { it.simpleName.contains(direction) }
        .map { it.constructors.first().newInstance() as T }
        .toList()

    val vector = CombinatoricsFactory.createVector(blocks)
    val generator = CombinatoricsFactory.createSubSetGenerator(vector)
    val result = mutableListOf<List<T>>()
    for (combination in generator) {
        result.add(combination.vector)
    }
    return result
}
