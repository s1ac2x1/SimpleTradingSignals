package com.kishlaly.ta.openai.flow

import com.kishlaly.ta.openai.CompletionRequest
import com.kishlaly.ta.openai.getCompletion
import java.nio.file.Files
import java.nio.file.Paths

class Step(
    val name: String,
    val input: List<String>
) {
    init {
        input.forEachIndexed { index, prompt ->
            val outputFileName = "step_${name}_${index + 1}"

            val completion = getCompletion(CompletionRequest(prompt = prompt))
            val postProcessed = contentRegex.replace(completion, "")

            Files.write(Paths.get("$outputFolder/$outputFileName"), postProcessed.toByteArray())
            println("$outputFileName finished")
        }
    }
}