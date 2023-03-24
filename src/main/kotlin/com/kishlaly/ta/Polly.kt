package com.kishlaly.ta

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

fun main() {
    val mainName = "random"
    var count = AtomicInteger(1)
    val srcFolder = "polly"
    val srcFile = "polly.txt"
    val files = File("$srcFolder/$srcFile").readLines()
    val executor = Executors.newFixedThreadPool(10)
    files
        //.take(20)
        .forEach { line ->
            executor.submit {
                val unique = UUID.randomUUID().toString()
                println("Processing ${count.getAndIncrement()}/${files.size}")
                generate(line, unique, "Tatyana", "ru-RU", 1, "ru", mainName)
                generate(line, unique, "Hans", "de-DE", 0, "de", mainName)
                combineMp3Files(
                    "$srcFolder/${mainName}_ru_${unique}.mp3",
                    "$srcFolder/${mainName}_de_${unique}.mp3",
                    "$srcFolder/${mainName}_full_${unique}.mp3",
                )
            }
        }
    executor.shutdown()
    executor.awaitTermination(1, TimeUnit.HOURS)
    merge(File("$srcFolder/").listFiles().filter { it.name.contains("_full_") }.map { it.absolutePath }.toList(), "$srcFolder/${mainName.uppercase()}.mp3")
    File("$srcFolder/").listFiles().filter { it.name.contains("_ru_") || it.name.contains("_ru_") }.forEach { it.delete() }
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
    val split = line.split("###")
    val pause = if (prefix.equals("de")) 2000 else 1000
    command.add("<speak><lang xml:lang=\"$lang\">${split[lineIndex]}</lang><break time=\"${pause}ms\"/></speak>")
    command.add("polly/${mainName}_${prefix}_${suffix}.mp3")
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
fun combineMp3Files(inputFile1: String, inputFile2: String, outputFile: String) {
    try {
        val inputStream1 = FileInputStream(File(inputFile1))
        val inputStream2 = FileInputStream(File(inputFile2))

        val outputStream = FileOutputStream(File(outputFile))

        val file1Bytes = inputStream1.readBytes()
        val file2Bytes = inputStream2.readBytes()

        outputStream.write(file1Bytes)
        outputStream.write(file2Bytes)

        inputStream1.close()
        inputStream2.close()
        outputStream.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}