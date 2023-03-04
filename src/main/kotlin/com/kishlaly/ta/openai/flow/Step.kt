package com.kishlaly.ta.openai.flow

import com.kishlaly.ta.openai.CompletionRequest
import com.kishlaly.ta.openai.ImageGenerator
import com.kishlaly.ta.openai.ImageTask
import com.kishlaly.ta.openai.getCompletion
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.atomic.AtomicInteger

enum class Type {
    TEXT,
    IMAGE
}

class Step(
    val name: String,
    val input: List<String> = emptyList(),
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
                    val outputFileName = "step_${name}_${index + 1}"
                    Files.write(Paths.get("$outputFolder/$outputFileName"), finalResult.toByteArray())
                }

                Type.IMAGE -> {
                    val imageTask = ImageTask(prompt, "Schwarz-Weiß-Bleistiftbild")
                    ImageGenerator.generate(listOf(imageTask), "$outputFolder")
                }
            }
            println("")
        }
    }
}