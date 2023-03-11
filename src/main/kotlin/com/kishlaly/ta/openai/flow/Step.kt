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
    TOC_PLAN,
    CONTENT_PART_1_HISTORY,
    CONTENT_PART_2_MAIN,
    CONTENT_PART_3_FACTS,
    OPPOSITE_OPINION_QUESTION,
    OPPOSITE_OPINION_TEXT,
    TAGS,
    TOC_IMAGES,
    FUTURED_IMAGE,
    FEATURED_IMAGE,
    CONCLUSION,
    RANDOM_ADDITION

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