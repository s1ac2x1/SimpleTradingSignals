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
            val completion = getCompletion(CompletionRequest(prompt = prompt))
            val postProcessed = contentRegex.replace(completion, "")
            Files.write(Paths.get("$outputFolder/${name}_${index + 1}"), postProcessed.toByteArray())
            println("Step ${name}_${index + 1} finished")
        }
    }
}