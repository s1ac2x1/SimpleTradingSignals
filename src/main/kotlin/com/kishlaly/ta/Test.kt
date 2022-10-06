package com.kishlaly.ta

import com.kishlaly.ta.analyze.tasks.blocks.two.ScreenTwoBlock
import org.paukov.combinatorics.CombinatoricsFactory
import org.paukov.combinatorics.Generator
import org.reflections.Reflections

fun main() {
//    val screenOneBlocks =
//        generateBlocksCombinations(
//            "com.kishlaly.ta.analyze.tasks.blocks.one",
//            clazz = ScreenOneBlock::class.java
//        )
//    println(screenOneBlocks.size)

    val twoScreenBlocksGenerator = generateBlocksCombinations(
        "com.kishlaly.ta.analyze.tasks.blocks.two",
        clazz = ScreenTwoBlock::class.java
    )
    var i = 0;
    for (combination in twoScreenBlocksGenerator) {
        println(i++)
    }
}

fun <T> generateBlocksCombinations(
    pckg: String,
    direction: String = "Long",
    clazz: Class<T>
): Generator<T> {
    var blocks = Reflections(pckg)
        .getSubTypesOf(clazz)
        .filter { it.simpleName.contains(direction) }
        .map { it.constructors.first().newInstance() as T }
        .toList()

    val vector = CombinatoricsFactory.createVector(blocks)
    return CombinatoricsFactory.createSubSetGenerator(vector)
}
