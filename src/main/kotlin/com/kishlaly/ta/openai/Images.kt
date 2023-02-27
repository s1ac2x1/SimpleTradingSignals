package com.kishlaly.ta.openai

import com.google.common.reflect.TypeToken
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.RequestBody
import java.io.FileOutputStream
import java.net.URL
import java.nio.channels.Channels
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

fun main() {
    val tasks = (1..150).map {
        var prompt = Combiner.combine(
            listOf(
                "openai/katze101/mood",
                "openai/katze101/breeds",
                "openai/katze101/age",
//                "openai/katze101/actions",
//                "openai/katze101/places"
            )
        )
        ImageTask(prompt, "in the style pencil artwork")
    }
    ImageGenerator.generate(tasks, "openai/output/images")
}

fun downloadFile(url: URL, outputFileName: String) {
    url.openStream().use {
        Channels.newChannel(it).use { rbc ->
            FileOutputStream(outputFileName).use { fos ->
                fos.channel.transferFrom(rbc, 0, Long.MAX_VALUE)
            }
        }
    }
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
        fun generate(tasks: List<ImageTask>, outputFolder: String) {
            val executor = Executors.newFixedThreadPool(5)
            for (task in tasks) {
                executor.submit {
                    val imageURL = getImageURL(ImageRequest(task.keyword + " " + task.style))
                    val outputFileName = filenameRegex.replace(task.keyword, "_") + "_" + System.currentTimeMillis()
                    downloadFile(URL(imageURL), "$outputFolder/$outputFileName.png")
                }
            }
            executor.shutdown()
            executor.awaitTermination(1, TimeUnit.HOURS)
        }
    }

}

data class ImageTask(val keyword: String, val style: String)

data class ImageRequest(
    val prompt: String,
    val n: Int = 1,
    val size: String = "512x512"
)