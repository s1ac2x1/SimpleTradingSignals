package com.kishlaly.ta

import java.io.File

fun main() {
    var count = 1
    File("polly.txt").readLines().chunked(20)
        .take(1)
        .forEach { chunk ->
            val request = StringBuilder("aws polly synthesize-speech --output-format mp3 --voice-id Hans --text-type ssml --text '<speak>")
            chunk.forEach { line ->
                val split = line.split("###")
                request.append("<lang xml:lang=\"ru-RU\">${split[1]}</lang><break time=\"1000ms\"/>")
                request.append("<lang xml:lang=\"de-DE\">${split[0]}</lang><break time=\"2500ms\"/>")
            }
            request.append("</speak>' random_${count++}.mp3")
            runAwsPollyCommand(request.toString())
    }
}

fun runAwsPollyCommand(command: String) {
    val processBuilder = ProcessBuilder(*command.split(" ").toTypedArray())
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