package com.kishlaly.ta.openai.flow

import java.io.File

val outputFolder = "openai/flow/output"
val contentRegex = Regex("\n\n")
val numericListRegex = Regex("\\d+\\. ")

val removeExtraLineBreaks: String.() -> String = { contentRegex.replace(this, "") }
val removeNumericList: String.() -> String = { numericListRegex.replace(this, "") }

fun main() {
    val initialKeyword = "Welche Katzen Haaren am wenigsten?"

    // введение
    Step(
        "1",
        listOf("eine Einleitung für einen Artikel zu einem Thema schreiben: $initialKeyword"),
        listOf(removeNumericList)
    )

//    // оглавление
//    Step("2", listOf("Schreiben Sie eine nummerierte Liste mit Schlüsselwörtern zum Thema \"$initialKeyword\"")) {
//        contentRegex.replace(this, "")
//        tocRegex.replace(this, "").trim()
//    }

    // пишем контент по пунктам оглавления
//    val prompts =
//        parseTOC("2_1").map { "Die Antwort auf die Frage, \"$initialKeyword\", ist die Antwort: \"$it\". Schreiben Sie eine ausführliche Expertenantwort auf dieses Thema. Begründen Sie Ihre Antwort gegebenenfalls mit einigen Beispielen" }
//    Step("3", prompts) {
//        contentRegex.replace(this, "")
//    }

    // картинка
    // можно добавить исторической справки по рандомны пунктам контента оглавления
}

fun parseTOC(stepNumber: String): List<String> {
    return File("$outputFolder/step_$stepNumber").readLines().map { numericListRegex.replace(it, "").trim() }
}
