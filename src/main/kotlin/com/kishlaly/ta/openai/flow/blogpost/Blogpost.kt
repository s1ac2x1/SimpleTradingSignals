package com.kishlaly.ta.openai.flow.blogpost

import com.kishlaly.ta.openai.PAA
import com.kishlaly.ta.openai.flow.Language
import com.kishlaly.ta.openai.flow.toFileName
import com.kishlaly.ta.openai.mainOutputFolder
import com.kishlaly.ta.openai.readCsv
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

val globalLanguage: Language = Language.EN
val globalBlogTopic = "cats"

fun main() {

    // Загрузить контент
    // Сгенерить картинки
    // Преобразовать в WebP
    // Загрузить все картинки в блог
    // Создать XML

    val xml = BlogpostXMLBuilder()
    readCSV("source").forEach { paa ->
        val meta = BlogpostContentMeta(
            keyword = paa.title,
            domain = "katze101.com",
            imgURI = "2023/03",
            imgSrcFolder = "openai/katze101/images_webp"
        )
        BlogpostDownloader(meta).download()
//        xml.append(meta)
//        Files.write(Paths.get("$mainOutputFolder/html/${paa.title.toFileName()}.html"),
//            htmlStub.replace("###content###", BlogpostContentBuilder(meta).build()).toByteArray())
//        Files.write(Paths.get("$mainOutputFolder/html/raw"),
//            BlogpostContentBuilder(meta).build().toByteArray())
    }
//    Files.write(Paths.get("$mainOutputFolder/posts_${System.currentTimeMillis()}.xml"), xml.build().toString().toByteArray())

//    val images = findAllImages(File("openai/flow/output"))
//    copyFilesToDirectory(images, File("openai/img"))
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