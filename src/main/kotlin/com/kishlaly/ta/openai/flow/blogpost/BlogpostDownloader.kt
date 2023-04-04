package com.kishlaly.ta.openai.flow.blogpost

import com.kishlaly.ta.openai.Combiner
import com.kishlaly.ta.openai.flow.*
import java.io.File
import kotlin.random.Random

class BlogpostDownloader(val meta: BlogpostContentMeta) {

    private val stepFolder =
        "openai/${meta.domain}/content/${meta.category}/${meta.type.name.lowercase()}/${meta.keywordSource.keyword.toFileName()}"

    fun downloadBigPost() {
        File(stepFolder).mkdir()

        introduction()
        tableOfContentsPlan()
        tableOfContentsTexts(Intent.TOC, Intent.TOC_PART_HISTORY)
        tableOfContentsTexts(Intent.TOC, Intent.TOC_PART_MAIN)
        tableOfContentsTexts(Intent.TOC, Intent.TOC_PART_FACTS)
        oppositeOpinionQuestion()
        oppositeOpinionText()
        tags()
        conclusion(Intent.INTRODUCTION)
        randomAddition()
    }

    fun downloadPAA() {
        File(stepFolder).mkdir()

        mainSection()
        historySection()
        factsSection()
        oppositeOpinionQuestion()
        oppositeOpinionText()
        conclusion(Intent.MAIN)
        randomAddition()
        tagsShort()
    }

    fun downloadPAA2() {
        File(stepFolder).mkdir()

        introduction()

        tableOfContentsPlanShort()
        tableOfContentsTexts(Intent.TOC_SHORT, Intent.TOC_PART_MAIN)
        tableOfContentsTexts(Intent.TOC_SHORT, Intent.TOC_PART_OWN_EXPERIENCE)

        tags()
        conclusionSimple(Intent.INTRODUCTION)
    }

    fun downloadMedium() {
//        File(stepFolder).mkdir()
//
//        introduction()
//        tableOfContentsPlan()
//        tableOfContentsTexts(Intent.TOC, Intent.TOC_PART_MAIN)
//        conclusion(Intent.INTRODUCTION)
        File(stepFolder).mkdir()

        introduction()

        tableOfContentsPlanShort()
        tableOfContentsTexts(Intent.TOC_SHORT, Intent.TOC_PART_MAIN)
        tableOfContentsTexts(Intent.TOC_SHORT, Intent.TOC_PART_OWN_EXPERIENCE)

        tags()
        conclusionSimple(Intent.INTRODUCTION)
    }

    fun downloadSavo() {
        File(stepFolder).mkdir()

        introduction()
        tableOfContentsPlanSavo()
        tableOfContentsTexts(Intent.TOC_SAVO, Intent.TOC_PART_MAIN)
        if (meta.keywordSource.text.isNotEmpty()) {
            savoCTA()
        }
    }

    private fun factsSection() {
        val intent = Intent.FACTS
        Step(
            intent = intent,
            folder = stepFolder,
            input = listOf(intent.get(globalLanguage, meta.keywordSource.keyword)),
            postProcessings = listOf(trimmed),
            useTone = globalUseTone,
        )
    }

    private fun mainSection() {
        val intent = Intent.MAIN
        Step(
            intent = intent,
            folder = stepFolder,
            input = listOf(intent.get(globalLanguage, meta.keywordSource.keyword)),
            postProcessings = listOf(trimmed),
            useTone = globalUseTone,
        )
    }

    private fun historySection() {
        val intent = Intent.HISTORY
        Step(
            intent = intent,
            folder = stepFolder,
            input = listOf(intent.get(globalLanguage, meta.keywordSource.keyword)),
            postProcessings = listOf(trimmed),
            useTone = globalUseTone,
        )
    }

    private fun randomAddition() {
        val conclusion =
            lineBreaksRegex.replace(readText(Intent.CONCLUSION), "")
        val intent = Intent.RANDOM_ADDITION
        val prompt = intent.get(globalLanguage, conclusion)
        val finalPrompt = if (Random.nextBoolean()) prompt.split("|||")[0] else prompt.split("|||")[1]
        Step(
            intent = intent,
            input = listOf(finalPrompt),
            folder = stepFolder,
            postProcessings = listOf(trimmed),
            useTone = globalUseTone,
        )
    }

    private fun conclusion(from: Intent) {
        val toMakeConclusionFrom = readText(from)
        val oppositeOpinion = readText(Intent.OPPOSITE_OPINION_TEXT)
        val prompt = lineBreaksRegex.replace(toMakeConclusionFrom, "") + " " + lineBreaksRegex.replace(oppositeOpinion, "")
        val intent = Intent.CONCLUSION
        Step(
            intent = intent,
            folder = stepFolder,
            input = listOf(intent.get(globalLanguage, prompt)),
            postProcessings = listOf(trimmed),
            useTone = globalUseTone,
        )
    }

    private fun conclusionSimple(from: Intent) {
        val toMakeConclusionFrom = readText(from)
        val prompt = lineBreaksRegex.replace(toMakeConclusionFrom, "")
        val intent = Intent.CONCLUSION
        Step(
            intent = intent,
            folder = stepFolder,
            input = listOf(intent.get(globalLanguage, prompt)),
            postProcessings = listOf(trimmed),
            useTone = globalUseTone,
        )
    }

    private fun featuredImageTask() {
        val intent = Intent.FEATURED_IMAGE_TASK
        Step(
            intent = intent,
            folder = stepFolder,
            input = listOf(intent.get(globalLanguage, meta.keywordSource.keyword)),
            postProcessings = listOf(trimmed),
        )
    }

    private fun featuredImage() {
        var prompt = Combiner.combine(
            listOf(
                "openai/katze101/breeds",
                "openai/katze101/age",
                "openai/katze101/behaviour",
                "openai/katze101/places",
            )
        )
        Step(
            intent = Intent.FEATURED_IMAGE,
            folder = stepFolder,
            type = Type.IMAGE,
            input = listOf("${prompt} in the style pencil artwork"),
            customImageName = "${meta.keywordSource.keyword.toFileName()}_${System.currentTimeMillis()}",
            imagesCount = 5
        )
    }

    private fun featuredImagesByTask(count: Int) {
        val prompt = readText(Intent.FEATURED_IMAGE_TASK)
        Step(
            intent = Intent.FEATURED_IMAGE,
            folder = stepFolder,
            type = Type.IMAGE,
            input = listOf("${prompt} In a style of a black and white pencil artwork"),
            customImageName = "${meta.keywordSource.keyword.toFileName()}_${System.currentTimeMillis()}",
            imagesCount = count
        )
    }

    private fun imagesForToC() {
        val prompts = readLines(Intent.TOC)
        Step(
            intent = Intent.TOC_IMAGES,
            folder = stepFolder,
            type = Type.IMAGE,
            input = prompts,
            imagesCount = 3
        )
    }

    private fun tags() {
        val prompt = readText(Intent.INTRODUCTION)
        val intent = Intent.TAGS
        Step(
            intent = intent,
            folder = stepFolder,
            input = listOf(intent.get(globalLanguage, prompt)),
            postProcessings = listOf(trimmed, removeDots),
        )
    }

    private fun tagsShort() {
        val prompt = readText(Intent.MAIN)
        val intent = Intent.TAGS_PAA
        Step(
            intent = intent,
            folder = stepFolder,
            input = listOf(intent.get(globalLanguage, prompt)),
            postProcessings = listOf(trimmed, removeDots),
        )
    }

    private fun oppositeOpinionText() {
        val prompt = readText(Intent.OPPOSITE_OPINION_QUESTION)
        val intent = Intent.OPPOSITE_OPINION_TEXT
        Step(
            intent = intent,
            folder = stepFolder,
            input = listOf(intent.get(globalLanguage, prompt)),
            postProcessings = listOf(trimmed),
            useTone = globalUseTone,
        )
    }

    private fun oppositeOpinionQuestion() {
        val intent = Intent.OPPOSITE_OPINION_QUESTION
        Step(
            intent = intent,
            folder = stepFolder,
            input = listOf(intent.get(globalLanguage, meta.keywordSource.keyword)),
            postProcessings = listOf(removeQuotes, removeDots, trimmed),
        )
    }

    private fun tableOfContentsTexts(tocIntent: Intent, contentIntent: Intent) {
        val prompt = readLines(tocIntent).map { contentIntent.get(globalLanguage, it) }
        Step(
            intent = contentIntent,
            folder = stepFolder,
            input = prompt,
            postProcessings = listOf(trimmed),
            useTone = globalUseTone,
        )
    }

    private fun tableOfContentsPlan() {
        val intent = Intent.TOC
        Step(
            intent = intent,
            folder = stepFolder,
            input = listOf(intent.get(globalLanguage, meta.keywordSource.keyword)),
            postProcessings = listOf(removeNumericList, filterBadTOC, removeQuestionMarks, trimmed)
        )
    }

    private fun tableOfContentsPlanShort() {
        val intent = Intent.TOC_SHORT
        Step(
            intent = intent,
            folder = stepFolder,
            input = listOf(intent.get(globalLanguage, meta.keywordSource.keyword)),
            postProcessings = listOf(removeNumericList, filterBadTOC, removeQuestionMarks, trimmed)
        )
    }

    private fun tableOfContentsPlanSavo() {
        val intent = Intent.TOC_SAVO
        Step(
            intent = intent,
            folder = stepFolder,
            input = listOf(intent.get(globalLanguage, meta.keywordSource.keyword)),
            postProcessings = listOf(removeNumericList, filterBadTOC, removeQuestionMarks, trimmed)
        )
    }

    private fun introduction() {
        val intent = Intent.INTRODUCTION
        Step(
            intent = intent,
            folder = stepFolder,
            input = listOf(intent.get(globalLanguage, meta.keywordSource.keyword)),
            postProcessings = listOf(trimmed),
            useTone = globalUseTone,
        )
    }

    private fun savoCTA() {
        val intent = Intent.EXTERNAL_PROMPT
        Step(
            intent = intent,
            folder = stepFolder,
            input = listOf(meta.keywordSource.text
                .replace("TITLE", meta.keywordSource.keyword)
                .replace("Please make a table of the advantages of these hosting companies (without html code)", "")
            ),
            postProcessings = listOf(trimmed)
        )
    }

    private fun resolveStepFileName(intent: Intent) = "$stepFolder/${intent}_1"

    private fun readText(intent: Intent) = File(resolveStepFileName(intent)).readText()

    private fun readLines(intent: Intent) = File(resolveStepFileName(intent)).readLines()
}