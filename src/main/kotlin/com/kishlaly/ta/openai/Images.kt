package com.kishlaly.ta.openai

import com.google.common.reflect.TypeToken
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.RequestBody
import io.ktor.util.collections.*
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

fun main() {
    val tasks = (1..1).map {
        var prompt = Combiner.combine(
            listOf(
                "openai/katze101/mood",
                "openai/katze101/breeds",
                "openai/katze101/age",
//                "openai/katze101/actions",
//                "openai/katze101/places"
            )
        ) + "in the style retro artwork"
        ImageTask(prompt)
    }
    ImageGenerator.generate(tasks, "cats.txt")
}

fun getImageURL(imageRequest: ImageRequest): String {
    var result = ""
    try {
        val httpClient = OkHttpClient()
        httpClient.setConnectTimeout(5, TimeUnit.MINUTES)
        httpClient.setReadTimeout(5, TimeUnit.MINUTES)
        httpClient.setWriteTimeout(5, TimeUnit.MINUTES)
        println("Generating image: ${imageRequest.prompt}")
        val request = Request.Builder()
            .url("https://api.openai.com/v1/images/generations")
            .post(RequestBody.create(JSON, gson.toJson(imageRequest)))
            .header("Authorization", "Bearer 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")
            .build()
        val body = httpClient.newCall(request).execute().body().string()
        val completionRespone = gson.fromJson<ImageResponse>(body, object : TypeToken<ImageResponse>() {}.type)
        result = completionRespone.data.firstOrNull()?.url ?: ""
    } catch (e: Exception) {
        println("Image generation error: ${e.message}")
    } finally {
        return result
    }
}

class ImageGenerator {

    companion object {
        fun generate(tasks: List<ImageTask>, outputFileName: String) {
            val urls = ConcurrentSet<String>()
            val executor = Executors.newFixedThreadPool(5)
            for (task in tasks) {
                executor.submit {
                    urls.add(getImageURL(ImageRequest(task.prompt)))
                }
            }
            executor.shutdown()
            executor.awaitTermination(1, TimeUnit.HOURS)
            val output = urls.joinToString(separator = "\n") + "\n"
            Files.write(
                Paths.get("openai/output/images/$outputFileName"),
                output.toByteArray(),
                StandardOpenOption.APPEND
            )
        }
    }

}

data class ImageTask(val prompt: String)

data class ImageRequest(
    val prompt: String,
    val n: Int = 1,
    val size: String = "512x512"
)