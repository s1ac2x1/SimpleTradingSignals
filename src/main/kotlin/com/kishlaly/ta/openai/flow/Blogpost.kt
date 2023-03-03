package com.kishlaly.ta.openai.flow

import java.io.File

val outputFolder = "openai/flow/output"
val contentRegex = Regex("\n\n")
val tocRegex = Regex("\\d+\\. ")

fun main() {
    val initialKeyword = "Welche Katzen Haaren am wenigsten?"

//    // введение
//    Step("1", listOf("eine Einleitung für einen Artikel zu einem Thema schreiben: $initialKeyword"))
//
//    // оглавление
//    Step("2", listOf("Schreiben Sie eine nummerierte Liste mit Schlüsselwörtern zum Thema \"$initialKeyword\""))

//    // пишем контент по пунктам оглавления
//    Step("3", parseTOC("step_2"))
}

fun parseTOC(step: String): List<String> {
    return File("$outputFolder/$step").readLines().map { tocRegex.replace(it, "").trim() }
}
