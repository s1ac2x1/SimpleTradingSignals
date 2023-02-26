package com.kishlaly.ta.openai

import com.google.common.reflect.TypeToken
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.RequestBody
import io.ktor.util.collections.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

fun main() {
    val tasks = (1..3).map {
        var prompt = Combiner.combine(
            listOf(
                "openai/katze101/breeds",
                "openai/katze101/actions",
                "openai/katze101/places"
            )
        ) + "pencil style"
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
        print("Generating image: $${imageRequest.prompt} ... ")
        val request = Request.Builder()
            .url("https://api.openai.com/v1/images/generations")
            .post(RequestBody.create(JSON, gson.toJson(imageRequest)))
            .header("Authorization", "Bearer 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")
            .build()
        val body = httpClient.newCall(request).execute().body().string()
        val completionRespone = gson.fromJson<ImageResponse>(body, object : TypeToken<ImageResponse>() {}.type)
        result = completionRespone.data.firstOrNull()?.url ?: ""
        println("done")
    } catch (e: Exception) {
        println("error: ${e.message}")
    } finally {
        return result
    }
}

class ImageGenerator {

    companion object {
        fun generate(tasks: List<ImageTask>, outputFileName: String) {
            val urls = ConcurrentSet<String>()
            // TODO переделать на executor pool
            runBlocking {
                val jobs = ArrayList<Job>()
                val semaphore = Semaphore(10)
                for (task in tasks) {
                    jobs += launch {
                        semaphore.acquire()
                        try {
                            urls.add(getImageURL(ImageRequest(task.prompt)))
                        } finally {
                            semaphore.release()
                        }
                    }
                }
                jobs.joinAll()
            }
            Files.write(Paths.get("openai/output/images/$outputFileName"), urls.joinToString(separator = "\n").toByteArray())
        }
    }

}

data class ImageTask(val prompt: String)

data class ImageRequest(
    val prompt: String,
    val n: Int = 1,
    val size: String = "512x512"
)