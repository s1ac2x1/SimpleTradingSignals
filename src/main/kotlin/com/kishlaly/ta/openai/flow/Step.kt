package com.kishlaly.ta.openai.flow

import com.kishlaly.ta.openai.*
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.random.Random

enum class Type {
    TEXT,
    IMAGE
}

enum class Intent {
    INTRODUCTION,
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
}

class Step(
    val intent: Intent,
    val input: List<String> = emptyList(),
    val folder: String,
    val postProcessings: List<(String) -> String> = emptyList(),
    val type: Type = Type.TEXT,
    val fixGrammar: Boolean = false,
    val imagesCount: Int = 1,
    val useTone: Boolean = false,
    val customImageName: String = "image_${System.currentTimeMillis()}"
) {
    val fixPrompt = "Korrigieren Sie Grammatikfehler in diesem Text:"

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

                    if (fixGrammar) {
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