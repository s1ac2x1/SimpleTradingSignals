package com.kishlaly.ta.openai.flow

import kotlin.random.Random

val outputFolder = "openai/flow/output"
val contentRegex = Regex("\n\n")
val finalRegex = Regex("\n\n\n")
val numericListRegex = Regex("\\d+\\. ")

val trimmed: (String) -> String = { it.trim() }
val removeExtraLineBreaks: (String) -> String = { contentRegex.replace(it, "") }
val removeQuotes: (String) -> String = { it.replace("\"", "") }
val removeDots: (String) -> String = { it.replace(".", "") }
val removeNumericList: (String) -> String = { numericListRegex.replace(it, "") }
val createParagraphs: (String) -> String = {
    val output = StringBuilder()
    it.split(".").filter { !it.isNullOrBlank() }.map { it.trim() }.chunked(Random.nextInt(3, 5)).forEach { chunk ->
        chunk.forEach { output.append(it).append(". ") }
        output.append("\n\n")
    }
    output.toString()
}
val removeFirstSentence: (String) -> String = { str ->
    str.substring(str.indexOfFirst { it == '.' } + 1, str.length)
}

fun main() {
    val initialKeyword = "Welche Katzen Haaren am wenigsten?"

    // TODO считать токены и цену

    // введение
    Step(
        "1",
        listOf("eine Einleitung für einen Artikel zu einem Thema schreiben: $initialKeyword"),
        listOf(removeExtraLineBreaks, trimmed)
    )

//    // оглавление
//    Step(
//        "2",
//        listOf("Schreiben Sie eine nummerierte Liste mit Schlüsselwörtern zum Thema \"$initialKeyword\""),
//        listOf(removeExtraLineBreaks, removeNumericList, trimmed)
//    )
//
//    // пишем контент по пунктам оглавления
//    val prompts =
//        File("$outputFolder/step_2_1")
//            .readLines()
//            .map { "Die Antwort auf die Frage, \"$initialKeyword\", ist die Antwort: \"$it\". Schreiben Sie eine ausführliche Expertenantwort auf dieses Thema. Begründen Sie Ihre Antwort gegebenenfalls mit einigen Beispielen" }
//    Step(
//        "3",
//        prompts,
//        listOf(createParagraphs, removeFirstSentence, trimmed)
//    )
//
//    // пишем контент по пунктам оглавления, часть вторая
//    val prompts =
//        File("$outputFolder/step_2_1")
//            .readLines()
//            .map { "Schreiben Sie eine kurze historische Anmerkung zu diesem Thema: $it" }
//    Step(
//        "4",
//        prompts,
//        listOf(createParagraphs, trimmed)
//    )
//
//    // пишем контент по пунктам оглавления, часть третья
//    val prompts =
//        File("$outputFolder/step_2_1")
//            .readLines()
//            .map { "Schreiben Sie einige interessante Fakten über $it. Formatieren Sie den Text in Form von Absätzen ohne Zahlen" }
//    Step(
//        "5",
//        prompts,
//        listOf(createParagraphs, trimmed)
//    )
//
//    // ищем противоположное мнение
//    Step(
//        "6",
//        listOf("Finden Sie einen Schlüsselsatz, der das Gegenteil davon ist: \"$initialKeyword\""),
//        listOf(removeQuotes, removeDots, trimmed)
//    )
//
//    // пишет текст об этом противоположном мнении
//    val step_7_prompt = File("$outputFolder/step_6_1").readText()
//    Step(
//        "7",
//        listOf("$step_7_prompt Schreiben Sie zwei Absätze zu diesem Thema"),
//        listOf(createParagraphs, trimmed)
//    )
//
//    // собираем ключевые слова из вступления
//    val step_8_prompt = File("$outputFolder/step_1_1").readText()
//    Step(
//        "8",
//        listOf("Erstellen Sie aus diesem Text eine durch Kommas getrennte Liste mit 4 Schlüsselwörtern: $step_8_prompt"),
//        listOf(trimmed)
//    )
//
//    // картинка (вероятно, лучше по step_2 elements)
//    val imagesPrompt =
//        File("$outputFolder/step_2_1")
//            .readLines()
//    Step(
//        "9",
//        type = Type.IMAGE,
//        imagesPrompt
//    )
//
//    // картинка, которая пойдет на features
//    val featuresImagePrompt = initialKeyword
//    Step(
//        "9",
//        type = Type.IMAGE,
//        listOf(featuresImagePrompt)
//    )


    // в конце пройтись finalRegex
}
