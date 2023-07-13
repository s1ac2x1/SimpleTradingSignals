package com.kishlaly.ta

import java.io.File

fun main() {
    val name = "Was vom Heizungsgesetz übrig ist"
    val inputFile = File("test.html")
    val outputFile = File("/Users/volodymyr/Downloads/${name}.html")

    if (!inputFile.exists()) {
        println("Input file does not exist.")
        return
    }

    val text = inputFile.readText()
    val sentences = text.split(".")
    var newContent = ""

    for ((index, sentence) in sentences.withIndex()) {
        newContent += sentence.trim() + ". "
        if ((index + 1) % 3 == 0) {
            newContent += "<br><br>"
        }
    }

    outputFile.writeText(newContent)
    println("The content was written to the output file successfully.")
}
