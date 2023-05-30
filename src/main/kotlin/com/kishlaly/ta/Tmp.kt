package com.kishlaly.ta

import com.kishlaly.ta.openai.flow.chunked
import com.kishlaly.ta.openai.flow.encodeURL
import java.io.File

fun main() {
    val lines = mutableListOf<String>()
    File("temp.txt").readLines().forEach { line ->
        if (line.contains("http")) {
            lines.add(line)
        }
    }
    lines.forEach {
        if (it.length < 100) {
            println(it)
        }
    }
}