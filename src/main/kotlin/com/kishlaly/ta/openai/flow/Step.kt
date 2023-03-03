package com.kishlaly.ta.openai.flow

import com.kishlaly.ta.openai.CompletionRequest
import com.kishlaly.ta.openai.getCompletion
import java.nio.file.Files
import java.nio.file.Paths

val outputFolder = "openai/flow/output"
val contentRegex = Regex("\n\n\n")

class Step(
    val name: String,
    val input: String
) {
    fun run() {
        val completion = getCompletion(CompletionRequest(prompt = input))
        val postProcessed = contentRegex.replace(completion, "")
        Files.write(Paths.get("$outputFolder/${name}_output"), postProcessed.toByteArray())
        println("Step $name finished")
    }
}