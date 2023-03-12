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

        getPromptsMarkers(globalLanguage).forEach {
            if (content.contains(it)) {
                println("!!!!!!!!! Found prompt marker: $it")
                return ""
            }
        }

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
    val text = """
Hier sind einige Tipps und Beispiele, um Ihnen bei der Einrichtung eines Outdoor-Kletterbereichs für Katzen zu helfen: 1. Stellen Sie sicher, dass der Kletterbereich in einem abgeschirmten Bereich des Gartens oder auf dem Balkon angebracht wird.

Dies schützt Ihre Katze vor Gefahren von außerhalb des Bereichs und hilft ihr auch dabei, warm zu bleiben. Verwenden Sie robuste Materialien wie Holz oder Metall für den Kletterbereich.

Diese Materialien können die Elemente besser standhalten als andere Materialien und halten länger als Plastik oder Textilien. Bauen Sie mehrere Etagen in den Kletterbereich ein, damit Ihre Katze verschiedene Höhen erklimmen kann und nicht nur am Boden bleibt.

Dies gibt ihr mehr Abwechslung und macht es interessanter für sie. 4. Installieren Sie Sitzplätze im oberen Teil des Kletterturms oder bauen Sie Nester in den Wänden des Turms – dies gibt ihr mehr Möglichkeiten, um sicher überdacht zu sein. 5.    """.trimIndent()

    val chunks =  chunked(text)
    println(chunks)
}