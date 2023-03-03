package com.kishlaly.ta.openai.flow

import com.kishlaly.ta.openai.CompletionRequest
import com.kishlaly.ta.openai.getCompletion
import java.nio.file.Files
import java.nio.file.Paths

class Step(
    val name: String,
    val input: List<String>,
    val postProcessings: List<String.() -> String>
) {
    init {
        input.forEachIndexed { index, prompt ->
            val outputFileName = "step_${name}_${index + 1}"

            val rawResponse = getCompletion(CompletionRequest(prompt = prompt))
            var finalResult = rawResponse
            postProcessings.forEach {
                finalResult = it(finalResult)
            }

            Files.write(Paths.get("$outputFolder/$outputFileName"), finalResult.toByteArray())
            println("$outputFileName finished")
        }
    }
}