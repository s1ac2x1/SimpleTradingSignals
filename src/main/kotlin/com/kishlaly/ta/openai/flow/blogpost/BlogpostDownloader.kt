package com.kishlaly.ta.openai.flow.blogpost

import com.kishlaly.ta.openai.Combiner
import com.kishlaly.ta.openai.flow.*
import com.kishlaly.ta.openai.mainOutputFolder
import java.io.File
import kotlin.random.Random

fun main() {
    val text = """
Die Geschichte des Kratzens ist eine der ältesten menschlichen Beziehungen zu Katzen. Seit Jahrhunderten haben Menschen versucht, die natürliche Anziehungskraft zwischen Katzen und Menschen zu erforschen. Kratzen ist ein natürliches Verhalten bei Katzen, das sie als Mittel verwenden, um ihre Umgebung zu markieren und ihr Territorium abzustecken. 

Es wird angenommen, dass es für sie auch eine Art von Entspannung und Stressabbau sein kann. Einige Forscher glauben sogar, dass Kratzen Teil der natürlichen sozialen Interaktion zwischen Mensch und Tier sein könnte. In den frühesten Aufzeichnungen über Katzen wurde berichtet, dass Kratzer als Geschenke an die Götter geschickt wurden - als Zeichen des Respekts vor dem mystischen Wesen der Katze. 

Dieser Brauch existiert noch heute in vielerlei Form in vielerlei Ländern auf der ganzen Welt. Kratzer haben auch eine lange Geschichte im Umgang mit Haustieren: Sie wurden verwendet, um den Tieren beizubringen, wo sie schlafen oder essen sollten; um ihnen beizubringen, wo sie ihr Geschäft machen sollten; oder man benutzte sie als Mittel der Bestrafung für unerwünschtes Verhalten. Heutzutage versteht man jedoch besser die Bedeutung von Kratzern für die psychische und emotionale Gesundheit von Haustieren - sowohl beim Menschen als auch beim Tier - und es gibt spezielle Produkte auf dem Markt (Kratzbäume usw. 

), die diesem Zweck dienlich sein könnnen. Obwohl es schwer ist zu bestimmen, warum Katzen kratzen oder warum Menschen angezogen werden von diesem Verhalten – was immer der Grund sein mag – bleibt Kratzen eines der älteststen Beispiele für den tiefgreifendsten Teil unserer Beziehung mit unseren geliebten Fellfreundinnnen: Das Verständnis und Respekt voreinander!.
""".trimIndent()
    val result = createParagraphs(text)
    println(result)
}

class BlogpostDownloader(val meta: BlogpostContentMeta) {

    private val stepFolder = "$mainOutputFolder/${meta.keyword.toFileName()}"


    fun download() {
        File(stepFolder).mkdir()

//        introduction()
//
//        tableOfContentsPlan()

//
//        tableOfContentsTexts_part1()
//        tableOfContentsTexts_part2()
//        tableOfContentsTexts_part3()
//
//        oppositeOpinionQuestion()
//        oppositeOpinionText()
//
//        tags()
//
        featuredImage()
//
//        conclusion()
//
//        randomAddition()
    }

    private fun randomAddition() {
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
            folder = stepFolder,
            postProcessings = listOf(trimmed),
            useTone = true,
            fixTypos = true
        )
    }

    private fun conclusion() {
        val introduction = readText(Intent.INTRODUCTION)
        val oppositeOpinion = readText(Intent.OPPOSITE_OPINION_TEXT)
        val prompt = lineBreaksRegex.replace(introduction, "") + " " + lineBreaksRegex.replace(oppositeOpinion, "")
        Step(
            intent = Intent.CONCLUSION,
            folder = stepFolder,
            input = listOf("Schreiben Sie ein Fazit zu diesem Artikel: $prompt"),
            postProcessings = listOf(trimmed),
            useTone = true,
            fixTypos = true
        )
    }

    private fun featuredImage() {
//        var prompt = Combiner.combine(
//            listOf(
//                "openai/katze101/breeds",
//                "openai/katze101/age",
//                "openai/katze101/behaviour",
//                "openai/katze101/places",
//            )
//        )
//        Step(
//            intent = Intent.FEATURED_IMAGE,
//            folder = stepFolder,
//            type = Type.IMAGE,
//            input = listOf("${prompt} in the style pencil artwork"),
//            customImageName = "${meta.keyword.toFileName()}_${System.currentTimeMillis()}",
//            imagesCount = 3
//        )
        Step(
            intent = Intent.FEATURED_IMAGE,
            folder = stepFolder,
            type = Type.IMAGE,
            input = listOf(meta.keyword),
            customImageName = "${meta.keyword.toFileName()}_${System.currentTimeMillis()}",
            imagesCount = 5
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
        Step(
            intent = Intent.TAGS,
            folder = stepFolder,
            input = listOf("Erstellen Sie aus diesem Text eine durch Kommas getrennte Liste mit 5 Schlüsselwörtern: $prompt"),
            postProcessings = listOf(trimmed, removeDots)
        )
    }

    private fun oppositeOpinionText() {
        val prompt = readText(Intent.OPPOSITE_OPINION_QUESTION)
        Step(
            intent = Intent.OPPOSITE_OPINION_TEXT,
            folder = stepFolder,
            input = listOf("Ich schreibe einen Blog über Katzen. Schreiben Sie drei Absätze zu diesem Thema: \"$prompt\"."),
            postProcessings = listOf(trimmed),
            useTone = true,
            fixTypos = true
        )
    }

    private fun oppositeOpinionQuestion() {
        Step(
            intent = Intent.OPPOSITE_OPINION_QUESTION,
            folder = stepFolder,
            input = listOf("Finden Sie einen Schlüsselsatz, der das Gegenteil davon ist: \"${meta.keyword}\""),
            postProcessings = listOf(removeQuotes, removeDots, trimmed)
        )
    }

    private fun tableOfContentsTexts_part3() {
        val prompt = readLines(Intent.TOC_PLAN)
            //.map { "Die Antwort auf die Frage, \"${meta.keyword}\", ist die Antwort: \"$it\". Schreiben Sie interessante Fakten über dieses Thema. Formatieren Sie den Text in Form von Absätzen ohne Zahlen" }
            .map { "Ich schreibe einen Blog über Katzen. Schreiben Sie interessante Fakten über dieses Thema: \"$it\". Formatieren Sie den Text in Form von Absätzen ohne Zahlen." }
        Step(
            intent = Intent.CONTENT_PART_3_FACTS,
            folder = stepFolder,
            input = prompt,
            postProcessings = listOf(trimmed),
            useTone = true,
            fixTypos = true
        )
    }

    private fun tableOfContentsTexts_part2() {
        val prompt = readLines(Intent.TOC_PLAN)
            //.map { "Die Antwort auf die Frage, \"${meta.keyword}\", ist die Antwort: \"$it\". Schreiben Sie eine ausführliche Expertenantwort auf dieses Thema. Begründen Sie Ihre Antwort mit einigen Beispielen" }
            .map { "Ich schreibe einen Blog über Katzen. Schreiben Sie eine ausführliche Expertenantwort auf dieses Thema: \"$it\". Begründen Sie Ihre Antwort mit einigen Beispielen." }
        Step(
            intent = Intent.CONTENT_PART_2_MAIN,
            folder = stepFolder,
            input = prompt,
            postProcessings = listOf(trimmed),
            useTone = true,
            fixTypos = true
        )
    }

    private fun tableOfContentsTexts_part1() {
        val prompt = readLines(Intent.TOC_PLAN)
            //.map { "Die Antwort auf die Frage, \"${meta.keyword}\", ist die Antwort: \"$it\". Schreiben Sie eine historische Anmerkung zu diesem Thema." }
            .map { "Ich schreibe einen Blog über Katzen. Schreiben Sie eine lange historische Notiz zu diesem Thema: \"$it\"." }
        Step(
            intent = Intent.CONTENT_PART_1_HISTORY,
            folder = stepFolder,
            input = prompt,
            postProcessings = listOf(trimmed),
            useTone = true,
            fixTypos = true
        )
    }

    private fun tableOfContentsPlan() {
        Step(
            intent = Intent.TOC_PLAN,
            folder = stepFolder,
            input = listOf("Ich schreibe einen Artikel über Katzen. Das Thema ist: \"${meta.keyword}\". Schreiben Sie eine Liste mit 10 bis 15 kurzen Unterüberschriften."),
            postProcessings = listOf(removeNumericList, filterBadTOC, removeQuestionMarks, trimmed)
        )
    }

    private fun introduction() {
        Step(
            intent = Intent.INTRODUCTION,
            folder = stepFolder,
            input = listOf("Ich schreibe einen Artikel über Katzen. Der Titel des Artikels lautet: \"${meta.keyword}\" Schreiben Sie eine ausführliche Einführung zu diesem Artikel."),
            postProcessings = listOf(trimmed),
            useTone = true,
            fixTypos = true
        )
    }

    private fun resolveStepFileName(intent: Intent) = "$stepFolder/${intent}_1"

    private fun readText(intent: Intent) = File(resolveStepFileName(intent)).readText()

    private fun readLines(intent: Intent) = File(resolveStepFileName(intent)).readLines()
}