package com.kishlaly.ta.openai.flow.blogpost

import com.kishlaly.ta.openai.flow.*
import java.io.File
import kotlin.random.Random

class BlogpostContentBuilder(val meta: BlogpostContentMeta) {

    fun getRandomInterlink(type: ArticleType): String {
        return keywords[type]?.shuffled()?.random()?.let {
            "${getReadAlsoTitle()} <a href=\"https://${meta.domain}/${it.title.replace("?", "").encodeURL()}\">${it.title}</a>"
        } ?: ""
    }

    fun buildPAA(): String {
        val srcFolder = meta.resolveKeywordFolder()
        if (!File("$srcFolder").exists()) {
            throw RuntimeException("Nothing to build, $srcFolder doesn't exist")
        }

        val main = File("$srcFolder/${Intent.MAIN}_1").readText()
        val history = File("$srcFolder/${Intent.HISTORY}_1").readText()
        val facts = File("$srcFolder/${Intent.FACTS}_1").readText()

        var content = """
        <p>${processMainContent(main)}</p>
        <p>${processHistoricalContent(history)}</p>
        ${if (interlinkage) "<p><b>${getRandomInterlink(ArticleType.PAA)}</b></p>" else ""}
        <p>${processFactsContent(facts)}</p>
        ${if (interlinkage) "<p><b>${getRandomInterlink(ArticleType.PAA)}</b></p>" else ""}
    """.trimIndent()

        content = postProcessAndCheck(content)

        return content
    }

    fun buildLongPost(): String {
        val srcFolder = meta.resolveKeywordFolder()

        if (!File("$srcFolder").exists()) {
            throw RuntimeException("Nothing to build, $srcFolder doesn't exist")
        }

        val introduction = File("$srcFolder/${Intent.INTRODUCTION}_1").readText()
        val tocPlan = File("$srcFolder/${Intent.TOC_PLAN}_1").readLines()

        val tocContent = StringBuilder()
        tocPlan.forEachIndexed { index, item ->
            tocContent.append("<h2>$item</h2>")

            if (insertImages) {
                val images = File(meta.imgSrcFolder).listFiles().toList().shuffled().take(tocPlan.size)
                var imageURL = "https://${meta.domain}/wp-content/uploads/${meta.imgURI}/${images[index].name}"
                tocContent.append("<img src='$imageURL' alt='$item'></img>")
            }

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
                        headingContent.append(processMainContent(part))
                    }
                    if (intent == Intent.CONTENT_PART_3_FACTS) {
                        val factsContent = processFactsContent(part)
                        headingContent.append(factsContent)
                    }
                }
            tocContent.append(headingContent.toString())
            tocContent.append("${if (interlinkage) "<p><b>${getRandomInterlink(ArticleType.PAA)}</b></p>" else ""}")
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

        content = postProcessAndCheck(content)

        return content
    }

    private fun postProcessAndCheck(content: String): String {
        var content1 = content
        content1 = finalRegex.replace(content1, "")
        content1 = content1.replace("!.", "!")
        content1 = content1.replace("?", "? ")
        content1 = content1.replace(". ,", ".,")
        content1 = content1.replace("  ", " ")
        content1 = content1.replace("..", ".")
        content1 = content1.replace(" .", ".")
        content1 = content1.replace(": - ", ": ")
        content1 = content1.replace(". - ", ". ")
        content1 = removeNumberedLists2(content1)
        content1 = content1.replace("•", "<br>•")

        getPromptsMarkers().forEach {
            if (content1.contains(it)) {
                println("!!!!!!!!! Found prompt marker: $it")
                return ""
            }
        }
        return content1
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

    private fun processMainContent(part: String): String {
        val result = StringBuilder()
        var markedB = 0
        val markedBMax = Random.nextInt(2)
        chunked(part).forEachIndexed { index, chunk ->
            if (Random.nextBoolean() && markedB < markedBMax) {
                result.append("<p>${wrapOneSenenceInTag(chunk, "b")}</p>")
                markedB++
            } else {
                result.append("<p>${chunk.joinToString(". ")}.</p>")
            }
        }
        return result.toString()
    }
}

fun main() {
    val text = """
Kratzen ist eine alte Tradition, die vor Tausenden von Jahren begann. Die Menschen haben Katzen schon immer als treue Begleiter angesehen und sie wurden als heilige Tiere verehrt. Kratzen war ein Zeichen der Zuneigung und Wertschätzung für Katzen, das sich auf die spirituelle Bindung zwischen Mensch und Tier bezog. 

In der Antike glaubten die Ägypter, dass Katzen eine Verbindung zu den Göttern hatten und dass Kratzen ihnen Glück bringen konnte. In dieser Zeit wurden Katzen auch oft in Grabstätten gefunden, was auf ihre besondere Rolle in der Gesellschaft hinweist. 

Im Mittelalter begannen die Menschen, Kratzbäume zu bauen, um ihren Katzen mehr Freiheit zu geben und sie vor Gefahren zu schützen. Diese Bäume waren mit verschiedenen Materialien gebaut und konnten mit Schmuck verziert werden. 

Heutzutage ist Kratzverhalten immer noch sehr beliebt bei den meisten Katzensorten. Es ist eine natürliche Art des Spielens für sie und es gibt viele verschiedene Arten von Kratzbäumen, die Ihnen helfen können, Ihrer Katze mehr Komfort zu bieten.    """.trimIndent()
    val chunks =  chunked(text)
    println(chunks)
}