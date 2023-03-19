package com.kishlaly.ta.openai.flow

import com.kishlaly.ta.openai.flow.blogpost.globalCategory
import com.kishlaly.ta.openai.flow.blogpost.globalDomain
import com.kishlaly.ta.openai.flow.blogpost.globalLimit
import com.kishlaly.ta.openai.flow.blogpost.globalType
import com.kishlaly.ta.openai.readCsv
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

fun filterCSV() {
    val filePath = "openai/$globalDomain/content/$globalCategory/${globalCategory}_${globalType.name.lowercase()}.csv"
    val rawContent = readCsv(filePath)
    var filteredContent = rawContent.distinctBy { it.title }
    if (filteredContent.size > globalLimit) {
        filteredContent = filteredContent.shuffled().take(globalLimit)
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