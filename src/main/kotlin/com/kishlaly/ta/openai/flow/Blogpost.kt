package com.kishlaly.ta.openai.flow

import com.kishlaly.ta.openai.filenameRegex
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.random.Random

val mainOutputFolder = "openai/flow/output"

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
    it.split(".").filter { !it.isNullOrBlank() }.map { it.trim() }.chunked(Random.nextInt(2, 4)).forEach { chunk ->
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
    prepare(initialKeyword)

    val xml = StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
    xml.append("<output>")

    // inside start
    xml.append("<post>")
    xml.append("<title>")
    xml.append(initialKeyword)
    xml.append("</title>")

    xml.append("<post>${buildContent(initialKeyword)}</post>")

    xml.append("</post>")
    // loop end
    xml.append("</output>")

    Files.write(Paths.get("$mainOutputFolder/posts.xml"), xml.toString().toByteArray())
}

fun prepare(keyword: String) {
    val outputFolderPerStep = filenameRegex.replace(keyword, "_")
    introduction(keyword, outputFolderPerStep)

    tableOfContentsPlan(keyword, outputFolderPerStep)
    tableOfContentsTexts_part1(keyword, outputFolderPerStep)
    tableOfContentsTexts_part2(outputFolderPerStep)
    tableOfContentsTexts_part3(outputFolderPerStep)

    oppositeOpinionQuestion(keyword, outputFolderPerStep)
    oppositeOpinionText(outputFolderPerStep)

    keywords(outputFolderPerStep)

    imagesForToC(outputFolderPerStep)

    featuresImages(keyword, outputFolderPerStep)

    conclusion(outputFolderPerStep)

    randomAddition(outputFolderPerStep)
}

fun buildContent(title: String): String {

    val introduction = File("$mainOutputFolder/step_1_1").readText()
    val tocPlan = File("$mainOutputFolder/step_2_1").readLines()

    val tocContent = StringBuilder()
    tocPlan.forEachIndexed { index, item ->
        tocContent.append("<h2>$item</h2>")

        val imageName =
            File("$mainOutputFolder/").listFiles().find { it.name.contains(filenameRegex.replace(item, "_")) }?.name
                ?: ""
        var imageURL = "https://katze101.com/wp-content/uploads/2023/03/$imageName"
        tocContent.append("<img src='$imageURL'></img>")

        val content_step_3 =
            File(mainOutputFolder).listFiles().find { it.name.contains("step_3_${index + 1}") }?.readText() ?: ""
        val content_step_4 =
            File(mainOutputFolder).listFiles().find { it.name.contains("step_4_${index + 1}") }?.readText() ?: ""
        val content_step_5 =
            File(mainOutputFolder).listFiles().find { it.name.contains("step_5_${index + 1}") }?.readText() ?: ""

        tocContent.append("<p>$content_step_3</p>")
        tocContent.append("<p>$content_step_4</p>")
        tocContent.append("<p>$content_step_5</p>")
    }

    val oppositeOpitionSubtitle = File("$mainOutputFolder/step_6_1").readText()
    val oppositeOpinionText = File("$mainOutputFolder/step_7_1").readText()

    val conclusion = File("$mainOutputFolder/step_10_1").readText()
    val randomAddition = File("$mainOutputFolder/step_11_1").readText()

    var content = """
        <p>$introduction</p>
        $tocContent
        <h2>$oppositeOpitionSubtitle</h2>
        <p>$oppositeOpinionText</p>
        <p>$conclusion</p>
        <p>$randomAddition</p>
    """.trimIndent()

    content = finalRegex.replace(content, "")
    content = content.replace("!.", ".")
    content = content.replace("!", ".")

    return content
}

fun randomAddition(outputFolderPerStep: String) {
    val conclusion = lineBreaksRegex.replace(File("$mainOutputFolder/step_10_1").readText(), "")
    var prompt = if (Random.nextBoolean()) {
        "Beschreiben Sie Ihre persönliche Erfahrung zu diesem Thema: $conclusion"
    } else {
        "Schreiben Sie im Auftrag der Redaktion unseres Blogs eine Stellungnahme zu diesem Thema: $conclusion"
    }
    Step(
        name = "11",
        input = listOf(prompt),
        outputFolder = outputFolderPerStep,
        postProcessings = listOf(createParagraphs, trimmed)
    )
}

fun conclusion(outputFolderPerStep: String) {
    val introduction = File("$mainOutputFolder/step_1_1").readText()
    val oppositeOpinion = File("$mainOutputFolder/step_7_1").readText()
    val prompt = lineBreaksRegex.replace(introduction, "") + " " + lineBreaksRegex.replace(oppositeOpinion, "")
    Step(
        name = "10",
        outputFolder = outputFolderPerStep,
        input = listOf("Schreiben Sie ein Fazit zu diesem Artikel: $prompt"),
        postProcessings = listOf(createParagraphs, trimmed)
    )
}

private fun featuresImages(initialKeyword: String, outputFolderPerStep: String) {
    val prompt = initialKeyword
    Step(
        name = "9",
        outputFolder = outputFolderPerStep,
        type = Type.IMAGE,
        input = listOf(prompt)
    )
}

private fun imagesForToC(outputFolderPerStep: String) {
    val prompt =
        File("$mainOutputFolder/step_2_1")
            .readLines()
    Step(
        name = "9",
        outputFolder = outputFolderPerStep,
        type = Type.IMAGE,
        input = prompt
    )
}

private fun keywords(outputFolderPerStep: String) {
    val prompt = File("$mainOutputFolder/step_1_1").readText()
    Step(
        name = "8",
        outputFolder = outputFolderPerStep,
        input = listOf("Erstellen Sie aus diesem Text eine durch Kommas getrennte Liste mit 4 Schlüsselwörtern: $prompt"),
        postProcessings = listOf(trimmed)
    )
}

private fun oppositeOpinionText(outputFolderPerStep: String) {
    val prompt = File("$mainOutputFolder/step_6_1").readText()
    Step(
        name = "7",
        outputFolder = outputFolderPerStep,
        input = listOf("$prompt Schreiben Sie zwei Absätze zu diesem Thema"),
        postProcessings = listOf(createParagraphs, trimmed)
    )
}

private fun oppositeOpinionQuestion(initialKeyword: String, outputFolderPerStep: String) {
    Step(
        "6",
        listOf("Finden Sie einen Schlüsselsatz, der das Gegenteil davon ist: \"$initialKeyword\""),
        listOf(removeQuotes, removeDots, trimmed)
    )
}

private fun tableOfContentsTexts_part3(outputFolderPerStep: String) {
    val prompt =
        File("$mainOutputFolder/step_2_1")
            .readLines()
            .map { "Schreiben Sie einige interessante Fakten über $it. Formatieren Sie den Text in Form von Absätzen ohne Zahlen" }
    Step(
        "5",
        prompt,
        listOf(createParagraphs, removeFirstSentence, trimmed)
    )
}

private fun tableOfContentsTexts_part2(outputFolderPerStep: String) {
    val prompt =
        File("$mainOutputFolder/step_2_1")
            .readLines()
            .map { "Schreiben Sie eine kurze historische Anmerkung zu diesem Thema: $it" }
    Step(
        "4",
        prompt,
        listOf(createParagraphs, trimmed)
    )
}

private fun tableOfContentsTexts_part1(initialKeyword: String, outputFolderPerStep: String) {
    val prompt =
        File("$mainOutputFolder/step_2_1")
            .readLines()
            .map { "Die Antwort auf die Frage, \"$initialKeyword\", ist die Antwort: \"$it\". Schreiben Sie eine ausführliche Expertenantwort auf dieses Thema. Begründen Sie Ihre Antwort gegebenenfalls mit einigen Beispielen" }
    Step(
        "3",
        prompt,
        listOf(createParagraphs, removeFirstSentence, trimmed)
    )
}

private fun tableOfContentsPlan(initialKeyword: String, outputFolderPerStep: String) {
    Step(
        "2",
        listOf("Schreiben Sie eine nummerierte Liste mit Schlüsselwörtern zum Thema \"$initialKeyword\""),
        listOf(removeExtraLineBreaks, removeNumericList, trimmed)
    )
}

private fun introduction(initialKeyword: String, outputFolderPerStep: String) {
    Step(
        "1",
        listOf("eine Einleitung für einen Artikel zu einem Thema schreiben: $initialKeyword"),
        listOf(removeExtraLineBreaks, trimmed)
    )
}
