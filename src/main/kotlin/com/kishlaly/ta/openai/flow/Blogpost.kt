package com.kishlaly.ta.openai.flow

import com.kishlaly.ta.openai.filenameRegex
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.random.Random

val outputFolder = "openai/flow/output"
val lineBreaksRegex = Regex("\n")
val contentRegex = Regex("\n\n")
val finalRegex = Regex("\n\n\n")
val numericListRegex = Regex("\\d+\\. ")

val trimmed: (String) -> String = { it.trim() }
val removeExtraLineBreaks: (String) -> String = { lineBreaksRegex.replace(it, "") }
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

//    introduction(initialKeyword)
//
//    tableOfContentsPlan(initialKeyword)
//    tableOfContentsTexts_part1(initialKeyword)
//    tableOfContentsTexts_part2()
//    tableOfContentsTexts_part3()

//    oppositeOpinionQuestion(initialKeyword)
//    oppositeOpinionText()
//
//    keywords()
//
//    imagesForToC()
//
//    featuresImages(initialKeyword)

//    conclusion()

//    randomAddition()

    buildContent()
}

fun buildContent() {
    val introduction = File("$outputFolder/step_1_1").readText()
    val tocPlan = File("$outputFolder/step_2_1").readLines()

    val tocContent = StringBuilder()
    tocPlan.forEachIndexed { index, item ->
        tocContent.append(item).append("\n\n")

        val imageName =
            File("$outputFolder/").listFiles().find { it.name.contains(filenameRegex.replace(item, "_")) }?.name ?: ""
        var imageURL = "https://katze101.com/wp-content/uploads/2023/03/$imageName"
        tocContent.append(imageURL).append("\n\n")

        val content_step_3 =
            File(outputFolder).listFiles().find { it.name.contains("step_3_${index + 1}") }?.readText() ?: ""
        val content_step_4 =
            File(outputFolder).listFiles().find { it.name.contains("step_4_${index + 1}") }?.readText() ?: ""
        val content_step_5 =
            File(outputFolder).listFiles().find { it.name.contains("step_5_${index + 1}") }?.readText() ?: ""

        tocContent.append(content_step_3).append("\n\n")
        tocContent.append(content_step_4).append("\n\n")
        tocContent.append(content_step_5).append("\n\n")
    }

    val oppositeOpitionSubtitle = File("$outputFolder/step_6_1").readText()
    val oppositeOpinionText = File("$outputFolder/step_7_1").readText()

    val conclusion = File("$outputFolder/step_10_1").readText()
    val randomAddition = File("$outputFolder/step_11_1").readText()

    var content = """
        $introduction \n\n
        $tocPlan \n\n
        $tocContent \n\n
        $oppositeOpitionSubtitle \n\n
        $oppositeOpinionText \n\n
        $conclusion \n\n
        $randomAddition \n\n
    """.trimIndent()

    content = finalRegex.replace(content, "")

    Files.write(Paths.get("$outputFolder/post.txt"), content.toByteArray())
}

fun randomAddition() {
    val conclusion = lineBreaksRegex.replace(File("$outputFolder/step_10_1").readText(), "")
    var prompt = if (Random.nextBoolean()) {
        "Beschreiben Sie Ihre persönliche Erfahrung zu diesem Thema: $conclusion"
    } else {
        "Schreiben Sie im Auftrag der Redaktion unseres Blogs eine Stellungnahme zu diesem Thema: $conclusion"
    }
    Step(
        "11",
        listOf(prompt),
        listOf(createParagraphs, trimmed)
    )
}

fun conclusion() {
    val introduction = File("$outputFolder/step_1_1").readText()
    val oppositeOpinion = File("$outputFolder/step_7_1").readText()
    val prompt = lineBreaksRegex.replace(introduction, "") + " " + lineBreaksRegex.replace(oppositeOpinion, "")
    Step(
        "10",
        listOf("Schreiben Sie ein Fazit zu diesem Artikel: $prompt"),
        listOf(createParagraphs, trimmed)
    )
}

private fun featuresImages(initialKeyword: String) {
    val prompt = initialKeyword
    Step(
        "9",
        type = Type.IMAGE,
        input = listOf(prompt)
    )
}

private fun imagesForToC() {
    val prompt =
        File("$outputFolder/step_2_1")
            .readLines()
    Step(
        "9",
        type = Type.IMAGE,
        input = prompt
    )
}

private fun keywords() {
    val prompt = File("$outputFolder/step_1_1").readText()
    Step(
        "8",
        listOf("Erstellen Sie aus diesem Text eine durch Kommas getrennte Liste mit 4 Schlüsselwörtern: $prompt"),
        listOf(trimmed)
    )
}

private fun oppositeOpinionText() {
    val prompt = File("$outputFolder/step_6_1").readText()
    Step(
        "7",
        listOf("$prompt Schreiben Sie zwei Absätze zu diesem Thema"),
        listOf(createParagraphs, trimmed)
    )
}

private fun oppositeOpinionQuestion(initialKeyword: String) {
    Step(
        "6",
        listOf("Finden Sie einen Schlüsselsatz, der das Gegenteil davon ist: \"$initialKeyword\""),
        listOf(removeQuotes, removeDots, trimmed)
    )
}

private fun tableOfContentsTexts_part3() {
    val prompt =
        File("$outputFolder/step_2_1")
            .readLines()
            .map { "Schreiben Sie einige interessante Fakten über $it. Formatieren Sie den Text in Form von Absätzen ohne Zahlen" }
    Step(
        "5",
        prompt,
        listOf(createParagraphs, removeFirstSentence, trimmed)
    )
}

private fun tableOfContentsTexts_part2() {
    val prompt =
        File("$outputFolder/step_2_1")
            .readLines()
            .map { "Schreiben Sie eine kurze historische Anmerkung zu diesem Thema: $it" }
    Step(
        "4",
        prompt,
        listOf(createParagraphs, trimmed)
    )
}

private fun tableOfContentsTexts_part1(initialKeyword: String) {
    val prompt =
        File("$outputFolder/step_2_1")
            .readLines()
            .map { "Die Antwort auf die Frage, \"$initialKeyword\", ist die Antwort: \"$it\". Schreiben Sie eine ausführliche Expertenantwort auf dieses Thema. Begründen Sie Ihre Antwort gegebenenfalls mit einigen Beispielen" }
    Step(
        "3",
        prompt,
        listOf(createParagraphs, removeFirstSentence, trimmed)
    )
}

private fun tableOfContentsPlan(initialKeyword: String) {
    Step(
        "2",
        listOf("Schreiben Sie eine nummerierte Liste mit Schlüsselwörtern zum Thema \"$initialKeyword\""),
        listOf(removeExtraLineBreaks, removeNumericList, trimmed)
    )
}

private fun introduction(initialKeyword: String) {
    Step(
        "1",
        listOf("eine Einleitung für einen Artikel zu einem Thema schreiben: $initialKeyword"),
        listOf(removeExtraLineBreaks, trimmed)
    )
}
