package com.kishlaly.ta.openai

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvParser
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.google.gson.Gson
import com.squareup.okhttp.MediaType
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

val apiKey = "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"
val gson = Gson()
val JSON = MediaType.parse("application/json; charset=utf-8")
val filenameRegex = Regex("[^A-Za-z0-9]")
val contentRegex = Regex("\n\n\n")

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
    val inputFile = "katzenrassen"
    val prompt =
        "Schreiben Sie eine ausführliche Expertenantwort auf die Frage ###title###. Verwenden Sie diese Informationen für den Kontext: ###context###"

    generateBlogArticles(inputFile, prompt)

}

fun generateBlogArticles(inputFileName: String, prompt: String) {
    val paas = readCsv(File("openai/$inputFileName.csv").inputStream())
        .distinctBy { it.title }.toList()

    val xml = StringBuffer("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>")
    xml.append("<output>")

    val executor = Executors.newFixedThreadPool(5)
    paas.forEach { executor.submit { createPostTag(it, prompt, xml) } }
    executor.shutdown()
    executor.awaitTermination(2, TimeUnit.HOURS)

    xml.append("<output>")
    Files.write(Paths.get("openai/output/text/$inputFileName.xml"), xml.toString().toByteArray())
}

private fun createPostTag(paaData: PAA, prompt: String, xml: StringBuffer) {
    println("Writing about: ${paaData.title}")

    var promptReplaced = prompt
        .replace("###title###", paaData.title)
        .replace("###context###", paaData.text)

    val completion = getCompletion(CompletionRequest(prompt = promptReplaced))
    val output = "${paaData.title}\n${completion}"
    val safeContent = contentRegex.replace(output, "\n\n")

    xml.append("<post>")

    xml.append("<title>")
    xml.append(paaData.title)
    xml.append("</title>")

    xml.append("<content>")
    xml.append(safeContent)
    xml.append("</content>")

    xml.append("<picture>")
    xml.append(getRandomWPURL("openai/output/images", "katze101.com", "2023/02"))
    xml.append("</picture>")

    xml.append("<post>")
}

//fun main() {
//    val imagePrompt = Combiner.combine(
//        listOf(
//            "openai/katze101/breeds",
//            //"openai/katze101/actions",
//            "openai/katze101/places"
//        )
//    )
//    println("Generating image [$imagePrompt]...")
//    val imageURL = getImageURL(ImageRequest(imagePrompt + "pencil style"))
//    println(imageURL)
//}

fun readCsv(inputStream: InputStream): List<PAA> =
    csvMapper.readerFor(PAA::class.java)
        .with(schema.withSkipFirstDataRow(true))
        .readValues<PAA>(inputStream)
        .readAll()

