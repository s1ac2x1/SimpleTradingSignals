package com.kishlaly.ta.openai.flow

import com.kishlaly.ta.openai.*
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.random.Random

enum class Language {
    DE
}

enum class Type {
    TEXT,
    IMAGE
}

enum class Intent(val map: Map<Language, String>) {
    INTRODUCTION(
        mapOf(
            Language.DE to "Ich schreibe einen Artikel über Katzen. Der Titel des Artikels lautet: \"###param###\" Schreiben Sie eine ausführliche Einführung zu diesem Artikel."
        )
    ),
    TOC_PLAN(
        mapOf(
            Language.DE to "Ich schreibe einen Artikel über Katzen. Das Thema ist: \"###param###\". Schreiben Sie eine Liste mit 10 bis 15 kurzen Unterüberschriften."
        )
    ),
    CONTENT_PART_1_HISTORY(
        mapOf(
            Language.DE to "Ich schreibe einen Blog über Katzen. Schreiben Sie eine lange historische Notiz zu diesem Thema: \"###param###\"."
        )
    ),
    CONTENT_PART_2_MAIN(
        mapOf(
            Language.DE to "Ich schreibe einen Blog über Katzen. Schreiben Sie eine ausführliche Expertenantwort auf dieses Thema: \"###param###\". Begründen Sie Ihre Antwort mit einigen Beispielen."
        )
    ),
    CONTENT_PART_3_FACTS(
        mapOf(
            Language.DE to "Ich schreibe einen Blog über Katzen. Schreiben Sie interessante Fakten über dieses Thema: \"###param###\". Formatieren Sie den Text in Form von Absätzen ohne Zahlen."
        )
    ),
    OPPOSITE_OPINION_QUESTION(
        mapOf(
            Language.DE to "Finden Sie einen Schlüsselsatz, der das Gegenteil davon ist: \"###param###\""
        )
    ),
    OPPOSITE_OPINION_TEXT(
        mapOf(
            Language.DE to "Ich schreibe einen Blog über Katzen. Schreiben Sie drei Absätze zu diesem Thema: \"###param###\"."
        )
    ),
    TAGS(
        mapOf(
            Language.DE to "Erstellen Sie aus diesem Text eine durch Kommas getrennte Liste mit 5 Schlüsselwörtern: \"###param###\""
        )
    ),
    TOC_IMAGES(
        mapOf(
            Language.DE to ""
        )
    ),
    FEATURED_IMAGE(
        mapOf(
            Language.DE to ""
        )
    ),
    CONCLUSION(
        mapOf(
            Language.DE to "Schreiben Sie ein Fazit zu diesem Artikel: \"###param###\""
        )
    ),
    RANDOM_ADDITION(
        mapOf(
            Language.DE to "Beschreiben Sie Ihre persönliche Erfahrung zu diesem Thema: \"###param###\"|||Schreiben Sie im Auftrag der Redaktion unseres Blogs eine Stellungnahme zu diesem Thema: \"###param###\""
        )
    );

    fun get(language: Language, paramValue: String) = map[language]!!.replace("###param###", paramValue)
}

class Step(
    val intent: Intent,
    val input: List<String> = emptyList(),
    val folder: String,
    val postProcessings: List<(String) -> String> = emptyList(),
    val type: Type = Type.TEXT,
    val fixTypos: Boolean = false,
    val imagesCount: Int = 1,
    val useTone: Boolean = false,
    val customImageName: String = "image_${System.currentTimeMillis()}"
) {
    val fixPrompt = "Korrigieren Sie die Rechtschreibfehler in diesem Text:"

    init {
        input.forEachIndexed { index, prompt ->
            var finalPrompt = prompt
            if (useTone) {
                val tone = listOf(
                    "Objektiv",
                    "Subjektiv",
                    "Beschreibend",
                    "Informativ",
                    "Unterhaltsam",
                    "Lyrisch",
                    "Humorvoll",
                    "Persönlich",
                    "Dramatisch",
                    "Kritisch"
                )
                finalPrompt = "$prompt Antwortton - ${tone[Random.nextInt(tone.size)]}"
            }
            println("[$type][$intent]")
            when (type) {
                Type.TEXT -> {
                    var completion = ""

                    try {
                        completion = getCompletion(finalPrompt)
                    } catch (e: OpenAIException) {
                        println("!!! Got empty response. Retrying...")
                        completion = getCompletion(finalPrompt)
                    }

                    if (fixTypos) {
                        try {
                            completion = getCompletion("$fixPrompt \"${removeAllLineBreaks(completion)}\"")
                        } catch (e: OpenAIException) {
                            println("!!! Got empty response. Trying...")
                            completion = getCompletion("$fixPrompt \"${removeAllLineBreaks(completion)}\"")
                        }
                    }
                    val outputFileName = "${intent}_${index + 1}"
                    Files.write(
                        Paths.get("$folder/$outputFileName"),
                        completion.toByteArray()
                    )
                }

                Type.IMAGE -> {
                    val imageGenerateTask =
                        ImageGenerateTask(
                            keyword = prompt,
                            outputFolderName = "$folder",
                            outputFileName = customImageName,
                            n = imagesCount
                        )
                    ImagesProcessor.generate(listOf(imageGenerateTask))
                }
            }
            println("")
        }
    }

    private fun getCompletion(prompt: String): String {
        val rawResponse = getCompletion(CompletionRequest(prompt = prompt))
        var finalResult = rawResponse
        postProcessings.forEach {
            finalResult = it(finalResult)
        }
        return finalResult
    }
}