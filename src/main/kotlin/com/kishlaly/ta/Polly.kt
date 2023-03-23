package com.kishlaly.ta

import java.io.File
import java.lang.StringBuilder

fun main() {
    val request = StringBuilder("aws polly synthesize-speech --output-format mp3 --voice-id Hans --text-type ssml --text '<speak>")
    File("polly.txt").readLines().take(1).forEach { line ->
        val split = line.split("###")
        request.append("<lang xml:lang=\"ru-RU\">${split[1]}</lang><break time=\"1000ms\"/>")
        request.append("<lang xml:lang=\"de-DE\">${split[0]}</lang><break time=\"2500ms\"/>")
    }
    request.append("</speak>' output.mp3")
    File("polly_request.txt").writeBytes(request.toString().toByteArray())
}