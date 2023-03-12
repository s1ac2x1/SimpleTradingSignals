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
        content = removeNumberedLists2(content)
        content = content.replace("•", "<br>•")

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
        Das bedeutet aber nicht, dass Sie keine regelmäßigen Sitzungen zum Krallenschneiden brauchen - im Gegenteil. Eine regelmäßige Pflege der Krallen hilft dabei, Schmerzen und Verletzungsrisiken zu vermeiden und stellt sicher, dass Ihr Haustier gesund bleibt. Um dies richtig zu machen, müssen Sie jedoch die richtige Technik anwenden: 1) Wenn möglich nehmen Sie einen professionellen Tierarztdienst in Anspruch: Ein professioneller Tierarztdienst kann Ihnen helfen beim Entfernung übermäßiger Nagelplatten sowie beim Trimmen der Endkralle (dem Bereich am Ende des Nagels).

        Dieser Service kann besonders hilfreich sein für Menschen mit Angst vor dem Schneiden von Tieren oder Menschen mit schlechteren Augenerkrankung (die den Zustand der Kralle nicht gut beurteilbar machen). 2) Wenn Sie lieber selbst Hand anlegend möchten: Verwendet speziell entwickelte Instrumente wie Scheren oder Kauterisatoren um den Nagel sauber abzuschneiden; stellen Sie sicher das man den Nagel bis auf den letzten Millimeter abschnitt; halten Sie den Zeigefinger über dem Schnitt um Blutungsrisiken zu minimieren; trimmen sie nur die Spitze des Nageldornes ohne weiter ins Fleisch einzudringnen; lass vor allem jeden Schritt immer langsam angehen und mach Pausenhalt falls deine Katze unruhig oder gestresst reagiert; belohnt deine Katze danach mit Streicheleinheitene um ihr Vertrauenz aufzu bauenne usw.
    """.trimIndent()
    val chunks =  chunked(text)
    println(chunks)
}