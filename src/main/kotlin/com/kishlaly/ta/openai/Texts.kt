package com.kishlaly.ta.openai

import com.google.common.reflect.TypeToken
import com.google.gson.annotations.SerializedName
import com.kishlaly.ta.openai.flow.timeoutRetry
import com.kishlaly.ta.utils.FileUtils
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.checkerframework.checker.units.qual.Temperature
import java.util.concurrent.TimeUnit

fun getCompletion(completionRequest: CompletionRequest): String {
    var result = ""
    try {
        val httpClient = OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .build()

        val request = Request.Builder()
            .url("https://api.openai.com/v1/completions")
            .post(RequestBody.create(JSON, gson.toJson(completionRequest)))
            .header("Authorization", "Bearer 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")
            .build()

        println(completionRequest.prompt)
        val body = timeoutRetry(httpClient, request)
        if (body == null) {
            throw RuntimeException("Didn't make it after 3 retries :(")
        }
        val completionRespone = gson.fromJson<CompletionRespone>(body, object : TypeToken<CompletionRespone>() {}.type)
        textTokensUsed.addAndGet(completionRespone.usage?.totalTokens ?: 0)
        printCosts()
        result = completionRespone.choices.firstOrNull()?.text!!
    } catch (e: Exception) {
        println("!!! Exception while getting completion: [${completionRequest.prompt}] : ${e.message}")
        throw OpenAIException("!!! Exception while getting completion: [${completionRequest.prompt}] : ${e.message}")
    } finally {
        return result
    }
}

data class CompletionRequest(
    val model: String = "text-davinci-003",

    val prompt: String,

    @SerializedName("max_tokens")
    val maxTokens: Int = 2048,

    @SerializedName("top_p")
    val topP: Double = 0.5,

    @SerializedName("frequency_penalty")
    val frequencyPenalty: Double = 0.8,

    val temperature: Double = 0.7,

    val user: String = "vladimir@kishlaly.com"
)

data class EditRequest(
    val model: String = "text-davinci-edit-001",
    val input: String,
    val instruction: String,
    val n: Int = 1,

    @SerializedName("top_p")
    val topP: Double = 0.5,

    val user: String = "vladimir@kishlaly.com"
)