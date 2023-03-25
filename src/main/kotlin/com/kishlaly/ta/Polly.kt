package com.kishlaly.ta

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

val delimiter = " ### "
val srcFolder = "polly"
val outputFolder = "output"
val srcFile = "b2_phrases.txt"
val outputFileName = srcFile.replace(".txt", "")

// какой язык идет первым в файле, например: "говорить - sprechen" или "sprechen - говорить"
val ruPhraseIndex = 2
val dePhraseIndex = 1

// TODO потом импорт в квизлет

fun main() {
    var count = AtomicInteger(1)
    val phrases = File("$srcFolder/$srcFile").readLines()
    val executor = Executors.newFixedThreadPool(15)
    File("$srcFolder/$outputFolder").mkdir()
    val filteredPhrases = phrases
        .filter { it.trim().isNotEmpty() }
        //.filter { it.length < 100 }
        .distinctBy { it.split(delimiter)[dePhraseIndex - 1] }.toList()
    println("\nFiltered ${phrases.size - filteredPhrases.size} duplicates\n")
    File("$srcFolder/$srcFile").writeBytes(filteredPhrases.joinToString("\n").toByteArray())
    filteredPhrases
        //.shuffled()
        //.take(4)
        .forEach { line ->
            executor.submit {
                val unique = UUID.randomUUID().toString()
                println("Processing ${count.getAndIncrement()}/${filteredPhrases.size}: ${line.split(delimiter)[dePhraseIndex - 1]}")
                generate(line, unique, "Tatyana", "ru-RU", ruPhraseIndex - 1, "ru", outputFileName)
                generate(line, unique, "Hans", "de-DE", dePhraseIndex - 1, "de", outputFileName)
                merge(
                    listOf("$srcFolder/$outputFolder/${outputFileName}_ru_${unique}.mp3", "$srcFolder/$outputFolder/${outputFileName}_de_${unique}.mp3"),
                    "$srcFolder/$outputFolder/${outputFileName}_full_${unique}.mp3",
                )
            }
        }
    executor.shutdown()
    executor.awaitTermination(1, TimeUnit.HOURS)
    merge(File("$srcFolder/$outputFolder").listFiles().filter { it.name.contains("_full_") }.map { it.absolutePath }.toList(), "$srcFolder/$outputFolder/${outputFileName}.mp3")
    File("$srcFolder/$outputFolder").listFiles()
        .filter { it.name.contains("_ru_") || it.name.contains("_de_") || it.name.contains("_full_") }
        .forEach { it.delete() }
}

@Synchronized
private fun generate(
    line: String,
    suffix: String,
    voice: String,
    lang: String,
    lineIndex: Int,
    prefix: String,
    mainName: String
) {
    val command = mutableListOf<String>()
    command.add("aws")
    command.add("polly")
    command.add("synthesize-speech")
    command.add("--output-format")
    command.add("mp3")
    command.add("--voice-id")
    command.add(voice)
    command.add("--text-type")
    command.add("ssml")
    command.add("--text")
    val split = line.split(delimiter)
    val pause = if (prefix.equals("de")) 2000 else 1000
    command.add("<speak><lang xml:lang=\"$lang\">${split[lineIndex]}.</lang><break time=\"${pause}ms\"/></speak>")
    command.add("$srcFolder/$outputFolder/${mainName}_${prefix}_${suffix}.mp3")
    runAwsPollyCommand(command)
}

@Synchronized
fun runAwsPollyCommand(command: List<String>) {
    val processBuilder = ProcessBuilder(*command.toTypedArray())
    processBuilder.redirectErrorStream(true)
    processBuilder.directory(File(System.getProperty("user.dir")))

    val process = processBuilder.start()
    val output = process.inputStream.bufferedReader().readText()
    val exitCode = process.waitFor()

    if (exitCode == 0) {
    } else {
        println("Error executing command:\n$output")
    }
}

@Synchronized
fun merge(inputFiles: List<String>, outputFile: String) {
    try {
        val outputStream = FileOutputStream(File(outputFile))

        inputFiles.forEach { inputFile ->
            FileInputStream(File(inputFile)).use { inputStream ->
                val fileBytes = inputStream.readBytes()
                outputStream.write(fileBytes)
            }
        }

        outputStream.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}