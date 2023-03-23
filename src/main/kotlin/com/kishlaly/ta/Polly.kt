package com.kishlaly.ta

import java.io.File

fun main() {
    var count = 1
    File("polly.txt").readLines().chunked(20)
        .take(1)
        .forEach { chunk ->
            chunk.forEach { line ->
                generate(line, count, "Tatyana", "ru-RU", 1, "ru")
                Thread.sleep(300)
            }
    }
}

private fun generate(line: String, count: Int, voice: String, lang: String, lineIndex: Int, prefix: String) {
    var count1 = count
    val command = mutableListOf<String>()
    command.add("aws")
    command.add("polly")
    command.add("synthesize-speech")
    command.add("--output-format")
    command.add("mp3")
    command.add("--voice-id")
    command.add(voice)
    command.add("Hans")
    command.add("--text-type")
    command.add("ssml")
    command.add("--text")
    val split = line.split("###")
    command.add("<speak><lang xml:lang=\"$lang\">${split[lineIndex]}</lang><break time=\"1000ms\"/></speak>")
    command.add("polly/random_${prefix}_${count1++}.mp3")
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