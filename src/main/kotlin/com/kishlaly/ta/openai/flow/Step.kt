package com.kishlaly.ta.openai.flow

import com.kishlaly.ta.openai.CompletionRequest
import com.kishlaly.ta.openai.getCompletion
import java.nio.file.Files
import java.nio.file.Paths

val outputFolder = "openai/flow/output"

class Step(
    val name: String,
    val input: String
) {
    fun run() {
        val completion = getCompletion(CompletionRequest(prompt = input))
        Files.write(Paths.get("$outputFolder/${name}_output"), completion!!.toByteArray())
    }
}