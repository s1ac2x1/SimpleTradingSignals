package com.kishlaly.ta.openai.flow.blogpost

import com.kishlaly.ta.openai.flow.*
import java.io.File
import kotlin.random.Random

class BlogpostContentBuilder(val meta: BlogpostContentMeta) {

    fun getRandomInterlink(type: ArticleType): String {
        return keywords[type]?.shuffled()?.random()?.let {
            "${getReadAlsoTitle()} <a href=\"https://${meta.domain}/${it.keyword.replace("?", "").encodeURL()}/\">${it.keyword}</a>"
        } ?: ""
    }

    fun buildPAA(): String {
        val srcFolder = meta.resolveKeywordFolder()
        if (!File("$srcFolder").exists()) {
            throw RuntimeException("Nothing to build, $srcFolder doesn't exist")
        }

        val mainSrc = File("$srcFolder/${Intent.MAIN}_1").readText()
        val historySrc = File("$srcFolder/${Intent.HISTORY}_1").readText()
        val factsSrc = File("$srcFolder/${Intent.FACTS}_1").readText()
        val oppositeQuestionTextSrc = File("$srcFolder/${Intent.OPPOSITE_OPINION_TEXT}_1").readText()
        val randomAdditionSrc = File("$srcFolder/${Intent.RANDOM_ADDITION}_1").readText()
        val conclusionSrc = File("$srcFolder/${Intent.CONCLUSION}_1").readText()

        val historyContent = """
            <h2>${getHistorySubtitle()}</h2>
            <p>${formatWith_B_U_I(historySrc)}</p>
        """.trimIndent()

        val factsContent = """
            <h2>${getFactsSubtitle()}</h2>
            <p>${formatWith_UL(factsSrc)}</p>
        """.trimIndent()

        val anotherOpinionContent = """
            <h2>${getAnotherOpitonSubtitle()}</h2>
            <p>${formatWith_B_U_I(oppositeQuestionTextSrc)}</p>
        """.trimIndent()

        val personalExperienceContent = """
            <h2>${getPersonalExperienceSubtitle()}</h2>
            <p>${formatWith_B_U_I(randomAdditionSrc)}</p>            
        """.trimIndent()

        val conclusionContent = """
            <h2>${getConclusionSubtitle()}</h2>
            <p>${formatWith_B(conclusionSrc)}</p>
        """.trimIndent()

        val contentParts = mutableSetOf(historyContent, factsContent, anotherOpinionContent, personalExperienceContent)

        var content = """
        <p>${formatWith_B(mainSrc)}</p>
        ${removeRandomElement(contentParts)}
        ${if (globalInterlinkage) "<p><b>${getRandomInterlink(ArticleType.PAA)}</b></p>" else ""}
        ${removeRandomElement(contentParts)}
        ${removeRandomElement(contentParts)}
        ${if (globalInterlinkage) "<p><b>${getRandomInterlink(ArticleType.PAA)}</b></p>" else ""}
        ${removeRandomElement(contentParts)}
        $conclusionContent
        ${if (globalInterlinkage) "<p><b>${getRandomInterlink(ArticleType.PAA)}</b></p>" else ""}
    """.trimIndent()

        content = postProcessAndCheck(content)

        return content
    }

    fun buildMedium(): String {
        val srcFolder = meta.resolveKeywordFolder()

        if (!File("$srcFolder").exists()) {
            throw RuntimeException("Nothing to build, $srcFolder doesn't exist")
        }

        val introduction = File("$srcFolder/${Intent.INTRODUCTION}_1").readText()
        val tocPlan = File("$srcFolder/${Intent.TOC}_1").readLines()

        val interlinksLimit = Random.nextInt(5) + 2
        var linksMade = 0
        val tocContent = StringBuilder()
        tocPlan.forEachIndexed { index, item ->
            tocContent.append("<h2>$item</h2>")

            val headingContent = StringBuilder()
            listOf(Intent.TOC_PART_MAIN).shuffled()
                .forEach { intent ->
                    val part =
                        File("$srcFolder").listFiles()
                            .find { file -> file.name.contains("${intent.name}_${index + 1}") }
                            ?.readText() ?: ""
                    if (intent == Intent.TOC_PART_MAIN) {
                        headingContent.append(formatWith_B(part))
                    }
                }
            tocContent.append(headingContent.toString())
        }

        var content = """
        <p>$introduction</p>
        $tocContent
    """.trimIndent()

        content = postProcessAndCheck(content)

        return content
    }

    fun buildSavo(): String {
        val srcFolder = meta.resolveKeywordFolder()

        if (!File("$srcFolder").exists()) {
            throw RuntimeException("Nothing to build, $srcFolder doesn't exist")
        }

        val introduction = File("$srcFolder/${Intent.INTRODUCTION}_1").readText()
        val tocPlan = File("$srcFolder/${Intent.TOC_SAVO}_1").readLines()

        val tocContent = StringBuilder()
        tocPlan.forEachIndexed { index, item ->
            tocContent.append("<h2>$item</h2>")

            val headingContent = StringBuilder()
            listOf(Intent.TOC_PART_MAIN).shuffled()
                .forEach { intent ->
                    val part =
                        File("$srcFolder").listFiles()
                            .find { file -> file.name.contains("${intent.name}_${index + 1}") }
                            ?.readText() ?: ""
                    if (intent == Intent.TOC_PART_MAIN) {
                        headingContent.append(if (Random.nextBoolean()) formatWith_B_U_I(part) else formatWith_UL(part))
                    }
                }
            tocContent.append(headingContent.toString())
        }

        val ctaFile = File("$srcFolder/${Intent.EXTERNAL_PROMPT}_1")
        val cta = if (ctaFile.exists()) "<p>" + ctaFile.readLines().filter { it.trim().length > 5 }.joinToString("<br><br>") + "</p>" else ""
        val disclosure = if (meta.keywordSource.text.isNotEmpty()) "<p><i>${disclosureGlobal}</i></p>" else ""

        var content = """
        $disclosure
        <p>${formatWith_B(introduction)}</p>
        $tocContent
        $cta
    """.trimIndent()

        return content
    }

    fun buildLongPost(): String {
        val srcFolder = meta.resolveKeywordFolder()

        if (!File("$srcFolder").exists()) {
            throw RuntimeException("Nothing to build, $srcFolder doesn't exist")
        }

        val introduction = File("$srcFolder/${Intent.INTRODUCTION}_1").readText()
        val tocPlan = File("$srcFolder/${Intent.TOC}_1").readLines()

        val interlinksLimit = Random.nextInt(5) + 2
        var linksMade = 0
        val tocContent = StringBuilder()
        tocPlan.forEachIndexed { index, item ->
            tocContent.append("<h2>$item</h2>")

            if (globalInsertImages) {
                val images = File(meta.imgSrcFolder).listFiles().toList().shuffled().take(tocPlan.size)
                var imageURL = "https://${meta.domain}/wp-content/uploads/${meta.imgURI}/${images[index].name}"
                tocContent.append("<img src='$imageURL' alt='$item'></img>")
            }

            val headingContent = StringBuilder()
            listOf(Intent.TOC_PART_HISTORY, Intent.TOC_PART_MAIN, Intent.TOC_PART_FACTS).shuffled()
                .forEach { intent ->
                    val part =
                        File("$srcFolder").listFiles()
                            .find { file -> file.name.contains("${intent.name}_${index + 1}") }
                            ?.readText() ?: ""
                    if (intent == Intent.TOC_PART_HISTORY) {
                        val historicalContent = formatWith_B_U_I(part)
                        headingContent.append(historicalContent)
                    }
                    if (intent == Intent.TOC_PART_MAIN) {
                        headingContent.append(formatWith_B(part))
                    }
                    if (intent == Intent.TOC_PART_FACTS) {
                        val factsContent = formatWith_UL(part)
                        headingContent.append(factsContent)
                    }
                }
            tocContent.append(headingContent.toString())
            if (globalInterlinkage && index % 3 == 0 && linksMade <= interlinksLimit) {
                tocContent.append("<p><b>${getRandomInterlink(ArticleType.PAA)}</b></p>")
                linksMade++
            }
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

    fun buildPAA2(): String {
        val srcFolder = meta.resolveKeywordFolder()

        if (!File("$srcFolder").exists()) {
            throw RuntimeException("Nothing to build, $srcFolder doesn't exist")
        }

        val introduction = File("$srcFolder/${Intent.INTRODUCTION}_1").readText()
        val tocPlan = File("$srcFolder/${Intent.TOC_SHORT}_1").readLines()

        val interlinksLimit = Random.nextInt(5) + 2
        var linksMade = 0
        val tocContent = StringBuilder()
        tocPlan.forEachIndexed { index, item ->
            tocContent.append("<h2>$item</h2>")

            if (globalInsertImages) {
                val images = File(meta.imgSrcFolder).listFiles().toList().shuffled().take(tocPlan.size)
                var imageURL = "https://${meta.domain}/wp-content/uploads/${meta.imgURI}/${images[index].name}"
                tocContent.append("<img src='$imageURL' alt='$item'></img>")
            }

            val headingContent = StringBuilder()
            listOf(Intent.TOC_PART_MAIN, Intent.TOC_PART_OWN_EXPERIENCE)
                .forEach { intent ->
                    val part =
                        File("$srcFolder").listFiles()
                            .find { file -> file.name.contains("${intent.name}_${index + 1}") }
                            ?.readText() ?: ""
                    if (intent == Intent.TOC_PART_MAIN) {
                        headingContent.append(formatWith_B_U_I(part))
                    }
                    if (intent == Intent.TOC_PART_OWN_EXPERIENCE) {
                        val factsContent = formatWith_UL(part)
                        headingContent.append(factsContent)
                    }
                }
            tocContent.append(headingContent.toString())
            if (globalInterlinkage && index % 3 == 0 && linksMade <= interlinksLimit) {
                tocContent.append("<p><b>${getRandomInterlink(ArticleType.PAA)}</b></p>")
                linksMade++
            }
        }

        val conclusion = File("$srcFolder/${Intent.CONCLUSION}_1").readText()

        var content = """
        <p>$introduction</p>
        $tocContent
        <p>$conclusion</p>
    """.trimIndent()

        content = postProcessAndCheck(content)

        return content
    }

    private fun removeRandomElement(set: MutableSet<String>): String {
        if (set.isEmpty()) {
            return ""
        }
        val randomElement = set.random()
        set.remove(randomElement)
        return randomElement
    }

    private fun postProcessAndCheck(content: String): String {
        var content1 = content
        content1 = finalRegex.replace(content1, "")
        content1 = content1.replace("!.", "!")
        content1 = content1.replace("!", ".")
        content1 = content1.replace("?", "? ")
        content1 = content1.replace(". ,", ".,")
        content1 = content1.replace("  ", " ")
        content1 = content1.replace("..", ".")
        content1 = content1.replace(" .", ".")
        content1 = content1.replace(": - ", ": ")
        content1 = content1.replace(". - ", ". ")
        content1 = content1.replace(": -", ": ")
        content1 = removeNumberedLists2(content1)
        content1 = content1.replace("•", "<br>•")
        content1 = content1.replace("\uFEFF", "")
        content1 = content1.replace(": )", ":)")
        content1 = content1.replace(":).", ":)")
        content1 = content1.replace(": (", ":(")
        content1 = content1.replace(":(.", ":(")

        getPromptsMarkers().forEach {
            if (content1.contains(it)) {
                println("!!!!!!!!! Found prompt marker: $it")
                return ""
            }
        }
        return content1
    }

    private fun formatWith_UL(part: String): String {
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

    private fun formatWith_B_U_I(part: String): String {
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

    private fun formatWith_B(part: String): String {
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
Es gibt viele verschiedene Spielzeuge, die Katzen lieben. Die besten Spielzeuge für Katzen sind solche, die ihren natürlichen Instinkten entsprechen und sie dazu anregen, zu spielen und zu jagen. Einige der besten Spielzeuge für Katzen sind Daumenkätzchen, Federangeln, Kratzbäume und Tunnel. 

Daumenkätzchen sind ein beliebtes Spielzeug für Katzen. Sie ähneln kleinen Mäusen oder Vögeln und können von Ihrer Katze gejagt werden. Diese Spielzeuge können in vielen verschiedenen Farben erworben werden und haben oft eine lange Schnur am Ende, so dass Sie mit Ihrer Katze spielen können. 

Federangeln sind auch ein sehr beliebtes Spielzeug für Katzen. Diese Art von Angel hat eine lange Feder an der Spitze des Stabs befestigt, so dass es möglich ist, damit zu "angeln". Dieses interessante Spiel hilft Ihrer Katze nicht nur dabei, fit zu bleiben und Spaß zu haben - es ermöglicht ihr auch den natürlichen Instinkt des Jägers auszuleben! 

Kratzbäume sind unverzichtbar für jede Wohnung mit einer oder mehreren Katzen. Kratzbäume bietet Ihnener Katzendie Möglichkeit, ihr natürliches Verhalten auszuleben - Klettern! Es gibt viele verschiedene Arten von Kratzbäumen: Einige haben Sisal-Stangenseile als Kletterstangenn; andere verfügen über mehrere Etagen mit Hütten oder Höhlenn; wieder andere bietendesigns in Form von Bäumen oder Strandthemen usw.. 

Tunnel sind weitere großartige OptionenfürKatzenspielwaren . Tunnel ermöglichen es IhnererKatzedas natürliche Verhalten des VersteckensundHinterherspringensauszuübenunddieKatzespieltmitdemVersteck-und-Suche-SpielaufintelligenteWeise. Es gibtdiverseArtenvonTunnelndieIhnererKatzegroßefreudenSchenkenkann:vomPopup-Karton bis hinzu speziell entwickeltePlastiktunnelndieaufeinemBodeneingebautwerdensollelnoderanWandseilegehangtwerdenkönne .
""".trimIndent()
    val chunks =  chunked(text)
    println(chunks)
}