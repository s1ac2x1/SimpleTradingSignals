package com.kishlaly.ta.openai.flow

import com.kishlaly.ta.openai.CompletionRequest
import com.kishlaly.ta.openai.getCompletion
import java.nio.file.Files
import java.nio.file.Paths

class Step(
    val name: String,
    val input: List<String>,
    val postProcessing: String.() -> String
) {
    init {
        input.forEachIndexed { index, prompt ->
            val outputFileName = "step_${name}_${index + 1}"

            val rawResponse = getCompletion(CompletionRequest(prompt = prompt))
            val result = postProcessing(rawResponse)

            Files.write(Paths.get("$outputFolder/$outputFileName"), result.toByteArray())
            println("$outputFileName finished")
        }
    }
}