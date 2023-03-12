package com.kishlaly.ta.openai.flow.blogpost

import com.kishlaly.ta.openai.PAA
import com.kishlaly.ta.openai.flow.Language
import com.kishlaly.ta.openai.flow.toFileName
import com.kishlaly.ta.openai.mainOutputFolder
import com.kishlaly.ta.openai.readCsv
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

val globalLanguage: Language = Language.DE
val globalBlogTopic = "Katzen"

fun main() {

    // Загрузить контент
    // Сгенерить картинки
    // Преобразовать в WebP
    // Загрузить все картинки в блог
    // Создать XML

    val xml = BlogpostXMLBuilder()
    readCSV("katzenrassen").shuffled().take(1).forEach { paa ->
        val meta = BlogpostContentMeta(
            keyword = paa.title,
            domain = "katze101.com",
            imgURI = "2023/03",
            imgSrcFolder = "openai/katze101/images_webp"
        )

        BlogpostDownloader(meta).downloadPAA()

//        buildContent(xml, meta, paa) {
//            BlogpostContentBuilder(it).buildPAA()
//        }
    }

//    Files.write(Paths.get("$mainOutputFolder/posts.xml"), xml.build().toString().toByteArray())

}

private fun buildContent(
    xml: BlogpostXMLBuilder,
    meta: BlogpostContentMeta,
    paa: PAA,
    builder: (meta: BlogpostContentMeta) -> String
) {
    xml.append(meta, builder)
    Files.write(
        Paths.get("$mainOutputFolder/html/${paa.title.toFileName()}.html"),
        htmlStub.replace("###content###", builder(meta)).toByteArray()
    )
    Files.write(
        Paths.get("$mainOutputFolder/html/_${paa.title.toFileName()}.raw"),
        builder(meta).toByteArray()
    )
}

fun findAllImages(rootDirectory: File): List<File> {
    val fileList = mutableListOf<File>()
    rootDirectory.walkTopDown().forEach { file ->
        if (file.isFile && file.name.contains(".png")) {
            fileList.add(file)
        }
    }
    return fileList
}

fun copyFilesToDirectory(files: List<File>, destinationDirectory: File) {
    files.forEach { file ->
        val fileName = file.name
        val destinationFile = File(destinationDirectory, fileName)

        file.copyTo(destinationFile)
    }
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

val htmlStub = """<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
</head>
<body>
###content###
</body>
</html>"""