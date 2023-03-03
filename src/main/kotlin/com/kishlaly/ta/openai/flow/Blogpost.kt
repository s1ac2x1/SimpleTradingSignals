package com.kishlaly.ta.openai.flow

import java.io.File
import kotlin.random.Random

val outputFolder = "openai/flow/output"
val contentRegex = Regex("\n\n")
val finalRegex = Regex("\n\n\n")
val numericListRegex = Regex("\\d+\\. ")

val trimmed: (String) -> String = { it.trim() }
val removeExtraLineBreaks: (String) -> String = { contentRegex.replace(it, "") }
val removeNumericList: (String) -> String = { numericListRegex.replace(it, "") }
val createParagraphs: (String) -> String = {
    val output = StringBuilder()
    it.split(".").filter { !it.isNullOrBlank() }.map { it.trim() }.chunked(Random.nextInt(3, 5)).forEach { chunk ->
        chunk.forEach { output.append(it).append(". ") }
        output.append("\n\n")
    }
    output.toString()
}

fun main() {
    val initialKeyword = "Welche Katzen Haaren am wenigsten?"

//    // введение
//    Step(
//        "1",
//        listOf("eine Einleitung für einen Artikel zu einem Thema schreiben: $initialKeyword"),
//        listOf(removeExtraLineBreaks, trimmed)
//    )

//    // оглавление
//    Step(
//        "2",
//        listOf("Schreiben Sie eine nummerierte Liste mit Schlüsselwörtern zum Thema \"$initialKeyword\""),
//        listOf(removeExtraLineBreaks, removeNumericList, trimmed)
//    )

    // пишем контент по пунктам оглавления
    val prompts =
        File("$outputFolder/step_2_1")
            .readLines()
            .map { "Die Antwort auf die Frage, \"$initialKeyword\", ist die Antwort: \"$it\". Schreiben Sie eine ausführliche Expertenantwort auf dieses Thema. Begründen Sie Ihre Antwort gegebenenfalls mit einigen Beispielen" }
    Step(
        "3",
        prompts,
        listOf(createParagraphs, trimmed)
    )

    // картинка

    // можно добавить исторической справки по рандомны пунктам контента оглавления

    // в конце пройтись finalRegex
}
