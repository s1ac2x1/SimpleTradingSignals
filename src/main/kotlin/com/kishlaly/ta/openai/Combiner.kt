package com.kishlaly.ta.openai

import java.io.File

class Combiner {

    // TODO нужно загрузить содержимое файлов один раз, а не открывать на каждый запрос

    companion object {
        fun combine(fileNames: List<String>): String {
            val contents = fileNames.map { fileName ->
                val lines = mutableListOf<String>()
                File(fileName).useLines { it.forEach { lines.add(it) } }
                lines
            }
            val output = StringBuffer()
            contents.forEach { content ->
                output.append(content.random()).append(" ")
            }
            return output.toString()
        }
    }

}

fun main() {
    val result = Combiner.combine(listOf("openai/katze101/breeds", "openai/katze101/actions", "openai/katze101/places"))
    println(result)
}