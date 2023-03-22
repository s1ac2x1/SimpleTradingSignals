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
        tableOfContentsTexts_history()
        tableOfContentsTexts_main()
        tableOfContentsTexts_facts()
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

    fun downloadMedium() {
        File(stepFolder).mkdir()

        introduction()
        tableOfContentsPlan()
        tableOfContentsTexts_main()
        conclusion(Intent.INTRODUCTION)
    }

    fun downloadSavo() {
        File(stepFolder).mkdir()

        introduction()
        tableOfContentsPlanSavo()
        tableOfContentsTextsSavo_main()
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
            useTone = true,
        )
    }

    private fun mainSection() {
        val intent = Intent.MAIN
        Step(
            intent = intent,
            folder = stepFolder,
            input = listOf(intent.get(globalLanguage, meta.keywordSource.keyword)),
            postProcessings = listOf(trimmed),
            useTone = true,
        )
    }

    private fun historySection() {
        val intent = Intent.HISTORY
        Step(
            intent = intent,
            folder = stepFolder,
            input = listOf(intent.get(globalLanguage, meta.keywordSource.keyword)),
            postProcessings = listOf(trimmed),
            useTone = true,
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
            useTone = true,
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
            useTone = true,
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
        val prompts = readLines(Intent.TOC_PLAN)
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
            useTone = true,
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

    private fun tableOfContentsTexts_facts() {
        val intent = Intent.CONTENT_PART_3_FACTS
        val prompt = readLines(Intent.TOC_PLAN).map { intent.get(globalLanguage, it) }
        Step(
            intent = intent,
            folder = stepFolder,
            input = prompt,
            postProcessings = listOf(trimmed),
            useTone = true,
        )
    }

    private fun tableOfContentsTexts_main() {
        val intent = Intent.CONTENT_PART_2_MAIN
        val prompt = readLines(Intent.TOC_PLAN).map { intent.get(globalLanguage, it) }
        Step(
            intent = intent,
            folder = stepFolder,
            input = prompt,
            postProcessings = listOf(trimmed),
            useTone = true,
        )
    }

    private fun tableOfContentsTextsSavo_main() {
        val intent = Intent.CONTENT_PART_2_MAIN
        val prompt = readLines(Intent.TOC_PLAN_SAVO).map { intent.get(globalLanguage, it) }
        Step(
            intent = intent,
            folder = stepFolder,
            input = prompt,
            postProcessings = listOf(trimmed),
            useTone = true,
        )
    }

    private fun tableOfContentsTexts_history() {
        val intent = Intent.CONTENT_PART_1_HISTORY
        val prompt = readLines(Intent.TOC_PLAN).map { intent.get(globalLanguage, it) }
        Step(
            intent = intent,
            folder = stepFolder,
            input = prompt,
            postProcessings = listOf(trimmed),
            useTone = true,
        )
    }

    private fun tableOfContentsPlan() {
        val intent = Intent.TOC_PLAN
        Step(
            intent = intent,
            folder = stepFolder,
            input = listOf(intent.get(globalLanguage, meta.keywordSource.keyword)),
            postProcessings = listOf(removeNumericList, filterBadTOC, removeQuestionMarks, trimmed)
        )
    }

    private fun tableOfContentsPlanSavo() {
        val intent = Intent.TOC_PLAN_SAVO
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
            useTone = true,
        )
    }

    private fun savoCTA() {
        val intent = Intent.EXTERNAL_PROMPT
        Step(
            intent = intent,
            folder = stepFolder,
            input = listOf(meta.keywordSource.text.replace("###title###", meta.keywordSource.keyword)),
            postProcessings = listOf(trimmed)
        )
    }

    private fun resolveStepFileName(intent: Intent) = "$stepFolder/${intent}_1"

    private fun readText(intent: Intent) = File(resolveStepFileName(intent)).readText()

    private fun readLines(intent: Intent) = File(resolveStepFileName(intent)).readLines()
}