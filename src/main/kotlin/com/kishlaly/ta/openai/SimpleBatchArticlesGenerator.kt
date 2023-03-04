package com.kishlaly.ta.openai

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.Gson
import com.squareup.okhttp.MediaType
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

val apiKey = "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"
val gson = Gson()
val JSON = MediaType.parse("application/json; charset=utf-8")
val filenameRegex = Regex("[^A-Za-z0-9]")
var threads = 5
var domain = ""
var date = ""
var DELIMITER = ";"

data class PAA(
    @field:JsonProperty("PAA Title") val title: String,
    @field:JsonProperty("Text") val text: String
) {
    constructor() : this("", "")
}

fun main() {
    val inputFile = "katzenpflege"
    val prompt =
        "Schreiben Sie eine ausführliche Expertenantwort auf die Frage: ###title### Begründen Sie Ihre Antwort gegebenenfalls mit einigen Beispielen"

    threads = 5
    domain = "katze101.com"
    date = "2023/03"

    generateBlogArticles(inputFile, prompt)
    //checkNull()
    //createSingleImportFile(inputFile)
}

fun checkNull() {
    File("openai/output/text").listFiles().forEach {
        if (it.readText().contains(">null")) {
            println("Error file: ${it.name}")
        }
    }
}

fun createSingleImportFile(fileName: String) {
    val xml = StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
    xml.append("<output>")
    File("openai/output/text").listFiles().forEach {
        xml.append(it.readText())
    }
    xml.append("</output>")
    Files.write(Paths.get("openai/$fileName.xml"), xml.toString().toByteArray())
}

fun generateBlogArticles(inputFileName: String, prompt: String) {
    val paas = try {
        readCsv("openai/$inputFileName.csv")
            .distinctBy { it.title }
            //.distinctBy { it.text }
            .toList()
    } catch (e: Exception) {
        throw e
    }

    val executor = Executors.newFixedThreadPool(threads)
    paas.forEach {
        executor.submit {
            try {
                createPostTag(it, prompt)
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    executor.shutdown()
    executor.awaitTermination(2, TimeUnit.HOURS)
}

private fun createPostTag(paaData: PAA, prompt: String) {
    val xml = StringBuilder()

    var promptReplaced = prompt
        .replace("###title###", paaData.title)
        .replace("###context###", paaData.text)

    val completion = getCompletion(CompletionRequest(prompt = promptReplaced))
    val output = "${completion}"
    val safeContent = contentRegex.replace(output, "\n\n")

    xml.append("<post>")

    xml.append("<title>")
    xml.append(paaData.title)
    xml.append("</title>")

    xml.append("<content>")
    xml.append(safeContent)
    xml.append("</content>")

    xml.append("<picture>")
    xml.append(getRandomWPURL("openai/output/images", domain, date))
    xml.append("</picture>")

    xml.append("</post>")

    val safeTitle = filenameRegex.replace(paaData.title, "_")
    Files.write(Paths.get("openai/output/text/$safeTitle-post.xml"), xml.toString().toByteArray())
    println("Ready: ${paaData.title}")
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

fun readCsv(fileName: String): List<PAA> =
    File(fileName).readLines().map { line ->
        val split = line.split(DELIMITER)
        if (split[0].contains("PAA Title")) {
            throw RuntimeException("Remote first line from CSV file!")
        }
        PAA(split[0], split[1])
    }
