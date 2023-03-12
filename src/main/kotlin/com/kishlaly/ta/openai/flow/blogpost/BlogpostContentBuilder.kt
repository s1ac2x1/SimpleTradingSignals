package com.kishlaly.ta.openai.flow.blogpost

import com.kishlaly.ta.openai.flow.*
import com.kishlaly.ta.openai.mainOutputFolder
import java.io.File
import kotlin.random.Random

class BlogpostContentBuilder(val meta: BlogpostContentMeta) {

    fun build(): String {
        val srcFolder = "$mainOutputFolder/${meta.keyword.toFileName()}"

        if (!File("$srcFolder").exists()) {
            throw RuntimeException("Nothing to build, $srcFolder doesn't exist")
        }

        val introduction = File("$srcFolder/${Intent.INTRODUCTION}_1").readText()
        val tocPlan = File("$srcFolder/${Intent.TOC_PLAN}_1").readLines()

        val images = File(meta.imgSrcFolder).listFiles().toList().shuffled().take(tocPlan.size)
        val tocContent = StringBuilder()
        tocPlan.forEachIndexed { index, item ->
            tocContent.append("<h2>$item</h2>")

            var imageURL = "https://${meta.domain}/wp-content/uploads/${meta.imgURI}/${images[index].name}"
            tocContent.append("<img src='$imageURL' alt='$item'></img>")

            val headingContent = StringBuilder()
            listOf(Intent.CONTENT_PART_1_HISTORY, Intent.CONTENT_PART_2_MAIN, Intent.CONTENT_PART_3_FACTS).shuffled()
                .forEach { intent ->
                    val part =
                        File("$srcFolder").listFiles()
                            .find { file -> file.name.contains("${intent.name}_${index + 1}") }
                            ?.readText() ?: ""
                    if (intent == Intent.CONTENT_PART_1_HISTORY) {
                        val historicalContent = processHistoricalContent(part)
                        headingContent.append(historicalContent)
                    }
                    if (intent == Intent.CONTENT_PART_2_MAIN) {
                        headingContent.append(createParagraphs(part))
                    }
                    if (intent == Intent.CONTENT_PART_3_FACTS) {
                        val factsContent = processFactsContent(part)
                        headingContent.append(factsContent)
                    }
                }
            tocContent.append(headingContent.toString())
        }

        val oppositeOpitionSubtitle = File("$srcFolder/${Intent.OPPOSITE_OPINION_QUESTION}_1").readText()
        val oppositeOpinionText = File("$srcFolder/${Intent.OPPOSITE_OPINION_TEXT}_1").readText()

        val conclusion = File("$srcFolder/${Intent.CONCLUSION}_1").readText()
        val randomAddition = File("$srcFolder/${Intent.RANDOM_ADDITION}_1").readText()

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
        content = content.replace(". ,", ".,")
        content = content.replace("  ", " ")
        content = content.replace("..", ".")
        content = content.replace(" .", ".")

        return content
    }

    private fun processFactsContent(part: String): String {
        val result = StringBuilder()
        val oneVariant = listOf(1, 3)
        val twoVariant = listOf(2, 4)
        val finalVariant = if (Random.nextBoolean()) oneVariant else twoVariant
        chunked(part).forEachIndexed { index, chunk ->
            if (index in finalVariant) {
                result.append("<p>${makeList(chunk)}</p>")
            } else {
                result.append("<p>${chunk.joinToString(". ")}.</p>")
            }
        }
        return result.toString()
    }

    private fun processHistoricalContent(part: String): String {
        val result = StringBuilder()
        var markedB = 0
        val markedBMax = Random.nextInt(2)
        var markedU = 0
        val markedUMax = Random.nextInt(2)
        var markedI = 0
        val markedIMax = Random.nextInt(2)
        chunked(part).forEachIndexed { index, chunk ->
            if (Random.nextBoolean() && markedB < markedBMax) {
                result.append("<p>${wrapOneSenenceInTag(chunk, "b")}</p>")
                markedB++
            } else if (Random.nextBoolean() && markedUMax < 2) {
                result.append("<p>${wrapOneSenenceInTag(chunk, "u")}</p>")
                markedU++
            } else if (Random.nextBoolean() && markedIMax < 2) {
                result.append("<p>${wrapOneSenenceInTag(chunk, "i")}</p>")
                markedI++
            } else {
                result.append("<p>${chunk.joinToString(". ")}.</p>")
            }
        }
        return result.toString()
    }
}

fun main() {
    val text = """und freundlich

Spielzeug und Beschäftigung sind für Katzen unerlässlich, um ein glückliches, gesundes Leben zu führen. Spielzeug hilft Katzen dabei, ihre natürlichen Instinkte auszuleben und bietet ihnen die Möglichkeit, sich zu beschäftigen und zu lernen. Es kann auch helfen, Stress abzubauen und Ängste zu lindern. Gleichzeitig kann es die Bindung zwischen Ihnen als Besitzer und Ihrer Katze stärken. 

Es gibt viele verschiedene Arten von Spielzeug für Katzen - von kleinen Bällchen bis hin zu interaktiven Spielgeräten wie dem Kratzbaum oder dem Kletterbaum. Wenn Sie eine neue Art von Spielzeug ausprobieren möchten, ist es am besten, wenn Sie mehrere Optionen anbieten. Dadurch erhalten Sie eine Vorstellung davon, was Ihrer Katze am besten gefallt. Es ist auch wichtig sicherzustellen, dass alle Teile des Spielzeugs in gutem Zustand sind - überprüfen Sie regelmäßig nach Verschleiß oder Schaden an den Teilen des Spiels. 

Neben dem Kauf neuer Spielsachen sollten Sie auch andere Wege finden, um Ihrer Katze Beschäftigungsmöglichkeiten anzubieten. Einige Beispiele hierfür sind: Versteck-Spiele spielen (versteckte Leckerlis oder Toys), tägliche Trainingssitzung (Kommando-Übung), interaktive Spiele (Laserpointer) oder Zeit mit der Familie verbringen (streicheln/kuscheln). All diese Aktivitäten helfen nicht nur dabei die mentale Stimulation Ihrer Katze zu verbessern; sondern stellen gleichsam eine starke Bindung her! 

In Kurzform: Spielzeug und Beschäftigung sind essentielle Bestandteile im Leben jeder Katze! Mit verschiedene Arten von Toys könnne Sie herausfinde welche Art am bestens gefallt; aber vergessen Sie nicht andere Wege der Stimulation wie Versteckspiele oder interaktive Spiele mit der Familienmitglieder!"""

}