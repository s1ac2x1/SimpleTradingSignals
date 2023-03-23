package com.kishlaly.ta

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

fun main() {
    val mainName = "random"
    var count = 1
    File("polly.txt").readLines().chunked(1)
        .take(1)
        .forEach { chunk ->
            chunk.forEach { line ->
                generate(line, count, "Tatyana", "ru-RU", 1, "ru", mainName)
                Thread.sleep(100)
                generate(line, count, "Hans", "de-DE", 0, "de", mainName)
                Thread.sleep(100)
                combineMp3Files(
                    "polly/${mainName}_ru_${count}.mp3",
                    "polly/${mainName}_de_${count}.mp3",
                    "polly/${mainName}_full_${count}.mp3",
                )
                count++
            }
        }
}

private fun generate(
    line: String,
    count: Int,
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
    val pause = if (prefix.equals("de")) 2500 else 1000
    command.add("<speak><lang xml:lang=\"$lang\">${split[lineIndex]}</lang><break time=\"${pause}ms\"/></speak>")
    command.add("polly/${mainName}_${prefix}_${count}.mp3")
    runAwsPollyCommand(command)
}

fun runAwsPollyCommand(command: List<String>) {
    val processBuilder = ProcessBuilder(*command.toTypedArray())
    processBuilder.redirectErrorStream(true)
    processBuilder.directory(File(System.getProperty("user.dir")))

    val process = processBuilder.start()
    val output = process.inputStream.bufferedReader().readText()
    val exitCode = process.waitFor()

    if (exitCode == 0) {
        println("Command executed successfully:\n$output")
    } else {
        println("Error executing command:\n$output")
    }
}

fun combineMp3Files(inputFile1: String, inputFile2: String, outputFile: String) {
    try {
        val inputStream1 = FileInputStream(File(inputFile1))
        val inputStream2 = FileInputStream(File(inputFile2))

        val outputStream = FileOutputStream(File(outputFile))

        // Read the contents of the input files
        val file1Bytes = inputStream1.readBytes()
        val file2Bytes = inputStream2.readBytes()

        // Combine the two files
        outputStream.write(file1Bytes)
        outputStream.write(file2Bytes)

        inputStream1.close()
        inputStream2.close()
        outputStream.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}