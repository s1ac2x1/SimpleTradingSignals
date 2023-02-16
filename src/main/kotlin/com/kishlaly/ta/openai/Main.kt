package com.kishlaly.ta.openai

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvParser
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.squareup.okhttp.MediaType
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.RequestBody
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.TimeUnit

val apiKey = "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"
val gson = Gson()
val JSON = MediaType.parse("application/json; charset=utf-8")

val csvMapper = CsvMapper().apply {
    enable(CsvParser.Feature.TRIM_SPACES)
    enable(CsvParser.Feature.SKIP_EMPTY_LINES)
}

val schema = CsvSchema.builder()
    .addColumn("PAA Title")
    .addColumn("Parent")
    .addColumn("Text")
    .addColumn("URL")
    .addColumn("URL Title")
    .build()

data class PAA(
    @field:JsonProperty("PAA Title") val title: String,
    @field:JsonProperty("Parent") val parent: String,
    @field:JsonProperty("Text") val text: String,
    @field:JsonProperty("URL") val url: String,
    @field:JsonProperty("URL Title") val urlTitle: String,
) {
    constructor() : this("", "", "", "", "")
}

fun main() {
    val paas = readCsv(File("paa.csv").inputStream())
        .distinctBy { it.title }.toList()

    paas.forEachIndexed { index, paaData ->
        print("[${index + 1}/${paas.size}] Writing about: ${paaData.title} ...")
        val request = CompletionRequest(
            prompt = "Please write a detailed article about ${paaData.title}. Use this information to write the article: ${paaData.text}"
        )
        val completion = getCompletion(request)
        val fileName = paaData.title.replace(" ", "_")
        Files.write(Paths.get("paa/${fileName}.txt"), completion!!.toByteArray())
        println(" done")
    }
}

data class CompletionRequest(
    val model: String = "text-davinci-003",
    val prompt: String,
    @SerializedName("max_tokens")
    val maxTokens: Int = 2048,
    @SerializedName("top_p")
    val topP: Double = 0.5
)

fun readCsv(inputStream: InputStream): List<PAA> =
    csvMapper.readerFor(PAA::class.java)
        .with(schema.withSkipFirstDataRow(true))
        .readValues<PAA>(inputStream)
        .readAll()

fun getCompletion(completionRequest: CompletionRequest): String? {
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
        val completionRespone = gson.fromJson<CompletionRespone>(body, object : TypeToken<CompletionRespone>() {}.type)
        return completionRespone.choices.firstOrNull()?.text
    } catch (e: Exception) {
        ""
    }
}
