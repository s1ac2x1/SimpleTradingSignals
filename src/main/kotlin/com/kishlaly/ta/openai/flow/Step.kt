package com.kishlaly.ta.openai.flow

import com.kishlaly.ta.openai.*
import java.nio.file.Files
import java.nio.file.Paths

enum class Type {
    TEXT,
    IMAGE
}

enum class Intent {
    INTRODUCTION,
    TOC_PLAN,
    CONTENT_PART1,
    CONTENT_PART2,
    CONTENT_PART3,
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
    val type: Type = Type.TEXT
) {
    init {
        input.forEachIndexed { index, prompt ->
            when (type) {
                Type.TEXT -> {
                    val rawResponse = getCompletion(CompletionRequest(prompt = prompt))
                    var finalResult = rawResponse
                    postProcessings.forEach {
                        finalResult = it(finalResult)
                    }
                    val outputFileName = "${intent}_${index + 1}"
                    Files.write(
                        Paths.get("$folder/$outputFileName"),
                        finalResult.toByteArray()
                    )
                }

                Type.IMAGE -> {
                    val imageGenerateTask =
                        ImageGenerateTask(
                            keyword = "Katze im Thema: \"$prompt\". Schwarz-Weiß-Zeichnung in Schraffurtechnik",
                            outputFolderName = "$folder",
                            outputFileName = prompt.toFileName()
                        )
                    ImagesProcessor.generate(listOf(imageGenerateTask))
                }
            }
            println("")
        }
    }
}