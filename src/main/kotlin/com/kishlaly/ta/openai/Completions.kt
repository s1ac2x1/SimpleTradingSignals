package com.kishlaly.ta.openai

import com.google.common.reflect.TypeToken
import com.google.gson.annotations.SerializedName
import com.kishlaly.ta.openai.flow.postWithRetry
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.util.concurrent.TimeUnit

fun getCompletion(completionRequest: CompletionRequest): String {
    var result = ""
    try {
        val httpClient = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .build()

        val request = Request.Builder()
            .url("https://api.openai.com/v1/completions")
            .post(RequestBody.create(JSON, gson.toJson(completionRequest)))
            .header("Authorization", "Bearer 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")
            .build()

        println(completionRequest.prompt)
        val body = postWithRetry(httpClient, request)
        val completionRespone = gson.fromJson<CompletionRespone>(body, object : TypeToken<CompletionRespone>() {}.type)
        textTokensUsed.addAndGet(completionRespone.usage?.totalTokens ?: 0)
        printCosts()
        result = completionRespone.choices.firstOrNull()?.text!!
    } catch (e: Exception) {
        println(e.message)
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
    val frequencyPenalty: Double = 0.8
)