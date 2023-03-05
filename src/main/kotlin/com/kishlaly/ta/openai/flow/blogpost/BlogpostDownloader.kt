package com.kishlaly.ta.openai.flow.blogpost

import com.kishlaly.ta.openai.filenameRegex
import com.kishlaly.ta.openai.flow.Intent
import com.kishlaly.ta.openai.flow.Step
import com.kishlaly.ta.openai.flow.Type
import com.kishlaly.ta.openai.lineBreaksRegex
import com.kishlaly.ta.openai.mainOutputFolder
import com.kishlaly.ta.openai.numericListRegex
import com.kishlaly.ta.text
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.random.Random

class BlogpostDownloader(val meta: BlogpostContentMeta) {

    private val trimmed: (String) -> String = { it.trim() }
    private val removeExtraLineBreaks: (String) -> String = { lineBreaksRegex.replace(it, "") }
    private val removeQuotes: (String) -> String = { it.replace("\"", "") }
    private val removeDots: (String) -> String = { it.replace(".", "") }
    private val removeNumericList: (String) -> String = { numericListRegex.replace(it, "") }
    private val removeQuestionMarks: (String) -> String = { it.replace("?", "") }
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

    private val stepFolder = "$mainOutputFolder/${filenameRegex.replace(meta.keyword, "_")}"

    fun download() {
        File(stepFolder).mkdir()

//        introduction()

        tableOfContentsPlan()
        val correctedToc = text.lines()
            .filter { it.trim().length >= 10 }
            .filter { it.length <= 100 }
            .filter { it.trim()[0].isLetter() }
            .filter { it.trim()[0].isUpperCase() }
            .joinToString("\n")
        println()
        println(correctedToc)
        println()
        Files.write(Paths.get(resolveStepFileName(Intent.TOC_PLAN)), correctedToc.toString().toByteArray())

        tableOfContentsTexts_part1()
        tableOfContentsTexts_part2()
        tableOfContentsTexts_part3()

        oppositeOpinionQuestion()
        oppositeOpinionText()

        tags()

        imagesQuestions()

        imagesForToC() // Das Bild sollte eine Katze zeigen, deren Verhalten mit dem Satz beschrieben werden kann: \"Vermeiden von Unruhe in der Umgebung der Katze\". Schwarz-Weiß-Bleistiftbild

        featuredImage()

        conclusion()

        randomAddition()
    }

    private fun imagesQuestions() {
        readLines(Intent.TOC_PLAN).map {
            ""
        }
    }

    fun randomAddition() {
        val conclusion =
            lineBreaksRegex.replace(readText(Intent.CONCLUSION), "")
        var prompt = if (Random.nextBoolean()) {
            "Beschreiben Sie Ihre persönliche Erfahrung zu diesem Thema: $conclusion"
        } else {
            "Schreiben Sie im Auftrag der Redaktion unseres Blogs eine Stellungnahme zu diesem Thema: $conclusion"
        }
        Step(
            intent = Intent.RANDOM_ADDITION,
            input = listOf(prompt),
            outputFolder = stepFolder,
            postProcessings = listOf(createParagraphs, trimmed)
        )
    }

    fun conclusion() {
        val introduction = readText(Intent.INTRODUCTION)
        val oppositeOpinion = readText(Intent.OPPOSITE_OPINION_TEXT)
        val prompt = lineBreaksRegex.replace(introduction, "") + " " + lineBreaksRegex.replace(oppositeOpinion, "")
        Step(
            intent = Intent.CONCLUSION,
            outputFolder = stepFolder,
            input = listOf("Schreiben Sie ein Fazit zu diesem Artikel: $prompt"),
            postProcessings = listOf(createParagraphs, trimmed)
        )
    }

    private fun featuredImage() {
        Step(
            intent = Intent.FEATURED_IMAGE,
            outputFolder = stepFolder,
            type = Type.IMAGE,
            input = listOf(meta.keyword)
        )
    }

    private fun imagesForToC() {
        val prompt = readLines(Intent.TOC_PLAN)
        Step(
            intent = Intent.TOC_IMAGES,
            outputFolder = stepFolder,
            type = Type.IMAGE,
            input = prompt
        )
    }

    private fun tags() {
        val prompt = readText(Intent.INTRODUCTION)
        Step(
            intent = Intent.TAGS,
            outputFolder = stepFolder,
            input = listOf("Erstellen Sie aus diesem Text eine durch Kommas getrennte Liste mit 5 Schlüsselwörtern: $prompt"),
            postProcessings = listOf(trimmed)
        )
    }

    private fun oppositeOpinionText() {
        val prompt = readText(Intent.OPPOSITE_OPINION_QUESTION)
        Step(
            intent = Intent.OPPOSITE_OPINION_TEXT,
            outputFolder = stepFolder,
            input = listOf("$prompt Schreiben Sie zwei Absätze zu diesem Thema"),
            postProcessings = listOf(createParagraphs, trimmed)
        )
    }

    private fun oppositeOpinionQuestion() {
        Step(
            intent = Intent.OPPOSITE_OPINION_QUESTION,
            outputFolder = stepFolder,
            input = listOf("Finden Sie einen Schlüsselsatz, der das Gegenteil davon ist: \"${meta.keyword}\""),
            postProcessings = listOf(removeQuotes, removeDots, trimmed)
        )
    }

    private fun tableOfContentsTexts_part3() {
        val prompt = readLines(Intent.TOC_PLAN)
            //.map { "Die Antwort auf die Frage, \"${meta.keyword}\", ist die Antwort: \"$it\". Schreiben Sie interessante Fakten über dieses Thema. Formatieren Sie den Text in Form von Absätzen ohne Zahlen" }
            .map { "Schreiben Sie interessante Fakten über dieses Thema: \"$it\". Formatieren Sie den Text in Form von Absätzen ohne Zahlen" }
        Step(
            intent = Intent.CONTENT_PART3,
            outputFolder = stepFolder,
            input = prompt,
            postProcessings = listOf(createParagraphs, trimmed)
        )
    }

    private fun tableOfContentsTexts_part2() {
        val prompt = readLines(Intent.TOC_PLAN)
            //.map { "Die Antwort auf die Frage, \"${meta.keyword}\", ist die Antwort: \"$it\". Schreiben Sie eine ausführliche Expertenantwort auf dieses Thema. Begründen Sie Ihre Antwort mit einigen Beispielen" }
            .map { "Schreiben Sie eine ausführliche Expertenantwort auf dieses Thema: \"$it\". Begründen Sie Ihre Antwort mit einigen Beispielen" }
        Step(
            intent = Intent.CONTENT_PART2,
            outputFolder = stepFolder,
            input = prompt,
            postProcessings = listOf(createParagraphs, trimmed)
        )
    }

    private fun tableOfContentsTexts_part1() {
        val prompt = readLines(Intent.TOC_PLAN)
            //.map { "Die Antwort auf die Frage, \"${meta.keyword}\", ist die Antwort: \"$it\". Schreiben Sie eine historische Anmerkung zu diesem Thema." }
            .map { "Schreiben Sie eine historische Anmerkung zu diesem Thema. \"$it\"" }
        Step(
            intent = Intent.CONTENT_PART1,
            outputFolder = stepFolder,
            input = prompt,
            postProcessings = listOf(createParagraphs, trimmed)
        )
    }

    private fun tableOfContentsPlan() {
        Step(
            intent = Intent.TOC_PLAN,
            outputFolder = stepFolder,
            input = listOf("Das Thema ist: \"${meta.keyword}\". Schreiben Sie eine Liste mit 10 bis 15 kurzen Unterüberschriften"),
            postProcessings = listOf(removeNumericList, removeQuestionMarks, trimmed)
        )
    }

    private fun introduction() {
        Step(
            intent = Intent.INTRODUCTION,
            outputFolder = stepFolder,
            input = listOf("eine Einleitung für einen Artikel zu einem Thema schreiben: ${meta.keyword}"),
            postProcessings = listOf(removeExtraLineBreaks, trimmed)
        )
    }

    private fun resolveStepFileName(intent: Intent) = "$stepFolder/${intent}_1"

    private fun readText(intent: Intent) = File(resolveStepFileName(intent)).readText()

    private fun readLines(intent: Intent) = File(resolveStepFileName(intent)).readLines()
}