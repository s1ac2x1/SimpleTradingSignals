package com.kishlaly.ta.openai

import com.google.gson.Gson
import com.squareup.okhttp.MediaType
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.RequestBody
import java.util.concurrent.TimeUnit

val apiKey = "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"
val gson = Gson()
val JSON = MediaType.parse("application/json; charset=utf-8")

fun main() {
    val request = CompletionRequest(
        prompt = "Please write a detailed article about what are the negative effects of yerba mate. Use this information to write the article: In the U.S., yerba mate is widely available in health food stores and online. People who recommend yerba mate say that it can relieve fatigue, aid in weight loss, ease depression, and help treat headaches and various other conditions. There's limited evidence that yerba mate may help with some of these conditions."
    )
    getCompletion(request)
}

data class CompletionRequest(
    val model: String = "text-davinci-003",
    val prompt: String,
    val max_tokens: Int = 2048,
    val top_p: Double = 0.5
)

fun getCompletion(completionRequest: CompletionRequest): String {
    return try {
        val httpClient = OkHttpClient()
        httpClient.setConnectTimeout(5, TimeUnit.MINUTES)
        httpClient.setReadTimeout(5, TimeUnit.MINUTES)
        httpClient.setWriteTimeout(5, TimeUnit.MINUTES)

        val request = Request.Builder()
            .url("https://api.openai.com/v1/completions")
            .post(RequestBody.create(JSON, gson.toJson(completionRequest)))
            .header("Authorization", "Bearer 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")
            .build()
        val body = httpClient.newCall(request).execute().body().string()
        return body
    } catch (e: Exception) {
        ""
    }
}
