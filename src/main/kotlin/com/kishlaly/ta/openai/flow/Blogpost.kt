package com.kishlaly.ta.openai.flow

import java.io.File

val outputFolder = "openai/flow/output"
val contentRegex = Regex("\n\n")
val tocRegex = Regex("\\d+\\. ")

fun main() {
    val initialKeyword = "Welche Katzen Haaren am wenigsten?"

//    // введение
//    Step("step_1", "eine Einleitung für einen Artikel zu einem Thema schreiben: $initialKeyword")
//
//    // оглавление
//    Step("step_2", "Schreiben Sie eine nummerierte Liste mit Schlüsselwörtern zum Thema \"$initialKeyword\"")

    // пишем контент по пунктам оглавления
    val tocItems: List<String> = parseTOC("step_2")
    println(tocItems)
}

fun parseTOC(step: String): List<String> {
    return File("$outputFolder/$step").readLines().map { tocRegex.replace(it, "").trim() }
}
