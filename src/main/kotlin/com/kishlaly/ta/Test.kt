package com.kishlaly.ta

import com.kishlaly.ta.analyze.tasks.blocks.one.ScreenOneBlock
import org.paukov.combinatorics.CombinatoricsFactory
import org.reflections.Reflections

fun main() {
    var screenOneBlocks = Reflections("com.kishlaly.ta.analyze.tasks.blocks.one")
        .getSubTypesOf(ScreenOneBlock::class.java)
        .filter { it.simpleName.contains("Long") }
        .map { it.constructors.first().newInstance() as ScreenOneBlock }
        .toList()

    for (screenOneBlock in screenOneBlocks) {
        println(screenOneBlock.javaClass.simpleName)
    }

    val vector = CombinatoricsFactory.createVector(screenOneBlocks)
    val generator = CombinatoricsFactory.createSubSetGenerator(vector)
    println(generator.numberOfGeneratedObjects)
//    for (combination in generator) {
//        println(combination)
//    }
}
