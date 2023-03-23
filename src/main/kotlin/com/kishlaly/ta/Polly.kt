package com.kishlaly.ta

import java.io.File

fun main() {
    var count = 1
    File("polly.txt").readLines().chunked(20)
        .take(1)
        .forEach { chunk ->
            chunk.forEach { line ->
                val command = mutableListOf<String>()
                //
                command.add("aws")
                command.add("polly")
                command.add("synthesize-speech")
                command.add("--output-format")
                command.add("mp3")
                command.add("--voice-id")
                command.add("Tatyana")
                command.add("--text-type")
                command.add("ssml")
                command.add("--text")
                val split = line.split("###")
                command.add("<speak><lang xml:lang=\"ru-RU\">${split[1]}</lang><break time=\"1000ms\"/></speak>")
                command.add("random_ru_${count++}.mp3")
                runAwsPollyCommand(command)
                Thread.sleep(100)
            }
    }
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