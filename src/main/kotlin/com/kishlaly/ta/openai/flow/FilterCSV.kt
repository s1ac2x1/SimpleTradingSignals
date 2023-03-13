package com.kishlaly.ta.openai.flow

import com.kishlaly.ta.openai.readCsv
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

fun filterCSV(name: String) {
    val rawContent = readCsv("openai/$name.csv")
    val filteredContent = rawContent.distinctBy { it.title }
    val lines = filteredContent.map { "${it.title};" }
    saveToFile("openai/$name.csv", lines)
    println("Filtered ${rawContent.size - filteredContent.size} duplicates")
}

fun saveToFile(filename: String, lines: List<String>) {
    val file = File(filename)
    val writer = BufferedWriter(FileWriter(file))

    for (line in lines) {
        writer.write(line)
        writer.newLine()
    }

    writer.close()
}