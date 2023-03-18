package com.kishlaly.ta.openai.flow

import com.kishlaly.ta.openai.domain
import com.kishlaly.ta.openai.flow.blogpost.category
import com.kishlaly.ta.openai.flow.blogpost.limit
import com.kishlaly.ta.openai.flow.blogpost.type
import com.kishlaly.ta.openai.readCsv
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

fun filterCSV() {
    val filePath = "openai/$domain/content/$category/${category}_${type.name.lowercase()}.csv"
    val rawContent = readCsv(filePath)
    var filteredContent = rawContent.distinctBy { it.title }
    if (filteredContent.size > limit) {
        filteredContent = filteredContent.shuffled().take(limit)
    }
    val lines = filteredContent.map { "${it.title};" }
    saveToFile(filePath, lines)
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