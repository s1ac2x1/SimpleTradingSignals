package com.kishlaly.ta.openai.flow.blogpost

import com.kishlaly.ta.openai.flow.*
import com.kishlaly.ta.openai.logFolder
import com.kishlaly.ta.openai.mainOutputFolder
import java.io.File
import kotlin.random.Random

fun main() {
    val text = """
        Um Ihrer Katze ein sicheres und stressfreies Zuhause zu bieten, ist es wichtig, dass Sie einige grundlegende Dinge beachten. Erstens sollten Sie sicherstellen, dass Ihre Katze ausreichend Platz hat, um sich zu bewegen und zu spielen. 

        Stellen Sie also sicher, dass Ihr Zuhause groß genug ist und mehrere Räume oder Bereiche hat, in denen Ihre Katze herumlaufen kann. Zweitens sollten Sie eine ruhige Umgebung schaffen. 

        Lautstärke kann für Katzen sehr stressig sein, daher sollten Sie versuchen, die Lautstärke in Ihrem Haus auf einem angenehmeren Niveau zu halten. Dazu gehört auch das Vermeiden von plötzlichen Geräuschen oder laute Musik. 

        Drittens müssen Sie auch an die Sicherheit denken: Stellen Sie sicher, dass alle Fenster geschlossen und alle Türen verschlossen sind - besonders wenn die Katze draußen ist - damit niemand unerwünscht in Ihr Haus gelangt oder die Katze entkommt. Viertens sollte man der Katze genug Zeit geben um zur Ruhe zu kommen und Stress abzubauen: Eine ruhige Ecke mit bequemen Kissen oder Deckchen für deine Katze kann hier helfen; aber es gibt noch viele andere Möglichkeiten wie Spiel- und Kratzmöbel sowie interaktive Spielgeräte für deine Samtpfote! 

        Schließlich muss man immer noch an den Komfort denken: Stellen Sie sicher, dass alle Betten sauber und weich sind; stellen Sie frisches Wasser bereit; halten Sie Futter-und Wasserschalen sauber; bietet ihn als regelmäßiges Fellpflegeprogramm an; bietet ihm Spielgeräte an (wie Bälle oder Seile) usw. 

        , damit er nicht langweilig wird!  All diese Dinge helfen ihm sein Zuhause als stressfreien Ort zu betrachten!.
    """.trimIndent()
    val result = removeAllLineBreaks(text)
    println(result)
}

class BlogpostDownloader(val meta: BlogpostContentMeta) {

    private val stepFolder = "$mainOutputFolder/${meta.keyword.toFileName()}"


    fun download() {
        File(stepFolder).mkdir()
        logFolder.set("$mainOutputFolder/${meta.keyword.toFileName()}/logs")
        File("$mainOutputFolder/${meta.keyword.toFileName()}/logs").mkdir()

        introduction()

        tableOfContentsPlan()

        tableOfContentsTexts_part1()
        tableOfContentsTexts_part2()
        tableOfContentsTexts_part3()

        oppositeOpinionQuestion()
        oppositeOpinionText()

        imagesForToC()

        tags()

        featuredImage()

        conclusion()

        randomAddition()
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
            postProcessings = listOf(createParagraphs, trimmed)
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
            postProcessings = listOf(createParagraphs, trimmed)
        )
    }

    private fun featuredImage() {
        Step(
            intent = Intent.FEATURED_IMAGE,
            folder = stepFolder,
            type = Type.IMAGE,
            input = listOf(meta.keyword)
        )
    }

    private fun imagesForToC() {
        val prompts = readLines(Intent.TOC_PLAN)
        Step(
            intent = Intent.TOC_IMAGES,
            folder = stepFolder,
            type = Type.IMAGE,
            input = prompts
        )
    }

    private fun tags() {
        val prompt = readText(Intent.INTRODUCTION)
        Step(
            intent = Intent.TAGS,
            folder = stepFolder,
            input = listOf("Erstellen Sie aus diesem Text eine durch Kommas getrennte Liste mit 5 Schlüsselwörtern: $prompt"),
            postProcessings = listOf(trimmed, removeDots),
            fixGrammar = false
        )
    }

    private fun oppositeOpinionText() {
        val prompt = readText(Intent.OPPOSITE_OPINION_QUESTION)
        Step(
            intent = Intent.OPPOSITE_OPINION_TEXT,
            folder = stepFolder,
            input = listOf("Ich schreibe einen Blog über Katzen. Schreiben Sie drei Absätze zu diesem Thema: \"$prompt\""),
            postProcessings = listOf(createParagraphs, trimmed)
        )
    }

    private fun oppositeOpinionQuestion() {
        Step(
            intent = Intent.OPPOSITE_OPINION_QUESTION,
            folder = stepFolder,
            input = listOf("Finden Sie einen Schlüsselsatz, der das Gegenteil davon ist: \"${meta.keyword}\""),
            postProcessings = listOf(removeQuotes, removeDots, trimmed),
            fixGrammar = false
        )
    }

    private fun tableOfContentsTexts_part3() {
        val prompt = readLines(Intent.TOC_PLAN)
            //.map { "Die Antwort auf die Frage, \"${meta.keyword}\", ist die Antwort: \"$it\". Schreiben Sie interessante Fakten über dieses Thema. Formatieren Sie den Text in Form von Absätzen ohne Zahlen" }
            .map { "Ich schreibe einen Blog über Katzen. Schreiben Sie interessante Fakten über dieses Thema: \"$it\". Formatieren Sie den Text in Form von Absätzen ohne Zahlen" }
        Step(
            intent = Intent.CONTENT_PART3,
            folder = stepFolder,
            input = prompt,
            postProcessings = listOf(createParagraphs, trimmed)
        )
    }

    private fun tableOfContentsTexts_part2() {
        val prompt = readLines(Intent.TOC_PLAN)
            //.map { "Die Antwort auf die Frage, \"${meta.keyword}\", ist die Antwort: \"$it\". Schreiben Sie eine ausführliche Expertenantwort auf dieses Thema. Begründen Sie Ihre Antwort mit einigen Beispielen" }
            .map { "Ich schreibe einen Blog über Katzen. Schreiben Sie eine ausführliche Expertenantwort auf dieses Thema: \"$it\". Begründen Sie Ihre Antwort mit einigen Beispielen" }
        Step(
            intent = Intent.CONTENT_PART2,
            folder = stepFolder,
            input = prompt,
            postProcessings = listOf(createParagraphs, trimmed)
        )
    }

    private fun tableOfContentsTexts_part1() {
        val prompt = readLines(Intent.TOC_PLAN)
            //.map { "Die Antwort auf die Frage, \"${meta.keyword}\", ist die Antwort: \"$it\". Schreiben Sie eine historische Anmerkung zu diesem Thema." }
            .map { "Ich schreibe einen Blog über Katzen. Schreiben Sie eine lange historische Notiz zu diesem Thema: \"$it\"" }
        Step(
            intent = Intent.CONTENT_PART1,
            folder = stepFolder,
            input = prompt,
            postProcessings = listOf(createParagraphs, trimmed)
        )
    }

    private fun tableOfContentsPlan() {
        Step(
            intent = Intent.TOC_PLAN,
            folder = stepFolder,
            input = listOf("Ich schreibe einen Artikel über Katzen. Das Thema ist: \"${meta.keyword}\". Schreiben Sie eine Liste mit 10 bis 15 kurzen Unterüberschriften"),
            postProcessings = listOf(removeNumericList, filterBadTOC, removeQuestionMarks, trimmed),
            fixGrammar = false
        )
    }

    private fun introduction() {
        Step(
            intent = Intent.INTRODUCTION,
            folder = stepFolder,
            input = listOf("Ich schreibe einen Artikel über Katzen. Der Titel des Artikels lautet: \"${meta.keyword}\" Schreiben Sie eine ausführliche Einführung zu diesem Artikel"),
            postProcessings = listOf(createParagraphs, trimmed)
        )
    }

    private fun resolveStepFileName(intent: Intent) = "$stepFolder/${intent}_1"

    private fun readText(intent: Intent) = File(resolveStepFileName(intent)).readText()

    private fun readLines(intent: Intent) = File(resolveStepFileName(intent)).readLines()
}