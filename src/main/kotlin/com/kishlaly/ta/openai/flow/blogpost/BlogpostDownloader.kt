package com.kishlaly.ta.openai.flow.blogpost

import com.kishlaly.ta.openai.filenameRegex
import com.kishlaly.ta.openai.flow.Step
import com.kishlaly.ta.openai.flow.Type
import com.kishlaly.ta.openai.lineBreaksRegex
import com.kishlaly.ta.openai.mainOutputFolder
import com.kishlaly.ta.openai.numericListRegex
import java.io.File
import kotlin.random.Random

class BlogpostDownloader(val meta: BlogpostContentMeta) {

    private val trimmed: (String) -> String = { it.trim() }
    private val removeExtraLineBreaks: (String) -> String = { lineBreaksRegex.replace(it, "") }
    private val removeQuotes: (String) -> String = { it.replace("\"", "") }
    private val removeDots: (String) -> String = { it.replace(".", "") }
    private val removeNumericList: (String) -> String = { numericListRegex.replace(it, "") }
    private val resolveShortKeyword: (String) -> String = {
        var shorter = it
        if (it.indexOf(':') > 0) {
            shorter = it.substring(0, it.indexOf(':'))
        }
        if (shorter.indexOf(';') > 0) {
            shorter = shorter.substring(0, shorter.indexOf(':'))
        }
        if (shorter.indexOf('-') in 0..3) {
            shorter = shorter.substring(shorter.indexOf('-') + 1, shorter.length)
        }
        shorter
    }
    private val createParagraphs: (String) -> String = {
        val output = StringBuilder()
        it.split(".").filter { !it.isNullOrBlank() }.map { it.trim() }.chunked(Random.nextInt(2, 4)).forEach { chunk ->
            chunk.forEach { output.append(it).append(". ") }
            output.append("\n\n")
        }
        output.toString()
    }
    private val removeFirstSentence: (String) -> String = { str ->
        str.substring(str.indexOfFirst { it == '.' } + 1, str.length)
    }

    private val outputFolder = filenameRegex.replace(meta.keyword, "_")

    fun download() {
        File("$mainOutputFolder/$outputFolder").mkdir()

        introduction()

        tableOfContentsPlan() // TODO может выдавать длинные фразы
        tableOfContentsTexts_part1()
        tableOfContentsTexts_part2()
        tableOfContentsTexts_part3()

        oppositeOpinionQuestion()
        oppositeOpinionText()

        keywords()

        imagesForToC() // картинки могут быть плохими TODO

        featuresImages()

        conclusion()

        randomAddition()
    }

    fun randomAddition() {
        val conclusion =
            lineBreaksRegex.replace(File("$mainOutputFolder/$outputFolder/step_10_1").readText(), "")
        var prompt = if (Random.nextBoolean()) {
            "Beschreiben Sie Ihre persönliche Erfahrung zu diesem Thema: $conclusion"
        } else {
            "Schreiben Sie im Auftrag der Redaktion unseres Blogs eine Stellungnahme zu diesem Thema: $conclusion"
        }
        Step(
            name = "11",
            input = listOf(prompt),
            outputFolder = outputFolder,
            postProcessings = listOf(createParagraphs, trimmed)
        )
    }

    fun conclusion() {
        val introduction = File("$mainOutputFolder/step_1_1").readText()
        val oppositeOpinion = File("$mainOutputFolder/step_7_1").readText()
        val prompt = lineBreaksRegex.replace(introduction, "") + " " + lineBreaksRegex.replace(oppositeOpinion, "")
        Step(
            name = "10",
            outputFolder = outputFolder,
            input = listOf("Schreiben Sie ein Fazit zu diesem Artikel: $prompt"),
            postProcessings = listOf(createParagraphs, trimmed)
        )
    }

    private fun featuresImages() {
        Step(
            name = "9",
            outputFolder = outputFolder,
            type = Type.IMAGE,
            input = listOf(meta.keyword)
        )
    }

    private fun imagesForToC() {
        val prompt =
            File("$mainOutputFolder/$outputFolder/step_2_1")
                .readLines()
        Step(
            name = "9",
            outputFolder = outputFolder,
            type = Type.IMAGE,
            input = prompt
        )
    }

    private fun keywords() {
        val prompt = File("$mainOutputFolder/step_1_1").readText()
        Step(
            name = "8",
            outputFolder = outputFolder,
            input = listOf("Erstellen Sie aus diesem Text eine durch Kommas getrennte Liste mit 4 Schlüsselwörtern: $prompt"),
            postProcessings = listOf(trimmed)
        )
    }

    private fun oppositeOpinionText() {
        val prompt = File("$mainOutputFolder/step_6_1").readText()
        Step(
            name = "7",
            outputFolder = outputFolder,
            input = listOf("$prompt Schreiben Sie zwei Absätze zu diesem Thema"),
            postProcessings = listOf(createParagraphs, trimmed)
        )
    }

    private fun oppositeOpinionQuestion() {
        Step(
            name = "6",
            outputFolder = outputFolder,
            input = listOf("Finden Sie einen Schlüsselsatz, der das Gegenteil davon ist: \"${meta.keyword}\""),
            postProcessings = listOf(removeQuotes, removeDots, trimmed)
        )
    }

    private fun tableOfContentsTexts_part3() {
        val prompt =
            File("$mainOutputFolder/$outputFolder/step_2_1")
                .readLines()
                .map { "Die Antwort auf die Frage, \"${meta.keyword}\", ist die Antwort: \"$it\". Schreiben Sie interessante Fakten über dieses Thema. Formatieren Sie den Text in Form von Absätzen ohne Zahlen" }
        Step(
            name = "5",
            outputFolder = outputFolder,
            input = prompt,
            postProcessings = listOf(createParagraphs, removeFirstSentence, trimmed)
        )
    }

    private fun tableOfContentsTexts_part2() {
        val prompt =
            File("$mainOutputFolder/$outputFolder/step_2_1")
                .readLines()
                .map { "Die Antwort auf die Frage, \"${meta.keyword}\", ist die Antwort: \"$it\". Schreiben Sie eine ausführliche Expertenantwort auf dieses Thema. Begründen Sie Ihre Antwort mit einigen Beispielen" }
        Step(
            name = "4",
            outputFolder = outputFolder,
            input = prompt,
            postProcessings = listOf(createParagraphs, trimmed)
        )
    }

    private fun tableOfContentsTexts_part1() {
        val prompt =
            File("$mainOutputFolder/$outputFolder/step_2_1")
                .readLines()
                .map { "Die Antwort auf die Frage, \"${meta.keyword}\", ist die Antwort: \"$it\". Schreiben Sie eine historische Anmerkung zu diesem Thema." }
        Step(
            name = "3",
            outputFolder = outputFolder,
            input = prompt,
            postProcessings = listOf(createParagraphs, removeFirstSentence, trimmed)
        )
    }

    private fun tableOfContentsPlan() {
        Step(
            name = "2",
            outputFolder = outputFolder,
            input = listOf("Schreiben Sie eine nummerierte Liste mit kurzen Stichwörtern zum Thema: \"${meta.keyword}\""),
            postProcessings = listOf(removeNumericList, resolveShortKeyword, trimmed)
        )
    }

    private fun introduction() {
        Step(
            name = "1",
            outputFolder = outputFolder,
            input = listOf("eine Einleitung für einen Artikel zu einem Thema schreiben: ${meta.keyword}"),
            postProcessings = listOf(removeExtraLineBreaks, trimmed)
        )
    }
}