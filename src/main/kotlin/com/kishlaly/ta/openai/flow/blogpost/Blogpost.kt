package com.kishlaly.ta.openai.flow.blogpost

import com.kishlaly.ta.openai.PAA
import com.kishlaly.ta.openai.mainOutputFolder
import com.kishlaly.ta.openai.readCsv
import java.nio.file.Files
import java.nio.file.Paths

fun main() {
    val xml = BlogpostXMLBuilder()
    readCSV("katzenrassen").shuffled().take(1).forEach { paa ->
        val meta = BlogpostContentMeta(
            keyword = paa.title,
            domain = "katze101.com",
            imgURI = "2023/03"
        )
        BlogpostDownloader(meta).download()
        xml.append(meta)
    }
    Files.write(Paths.get("$mainOutputFolder/posts.xml"), xml.build().toString().toByteArray())

    // теперь можно залить на хостинг картинки
    // TODO собрать все картинки из папок в одну
}

fun readCSV(inputFileName: String): List<PAA> {
    return try {
        readCsv("openai/$inputFileName.csv")
            .distinctBy { it.title }
            .toList()
    } catch (e: Exception) {
        throw e
    }
}
