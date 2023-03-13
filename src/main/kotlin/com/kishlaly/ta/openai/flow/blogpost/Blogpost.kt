package com.kishlaly.ta.openai.flow.blogpost

import com.kishlaly.ta.openai.PAA
import com.kishlaly.ta.openai.flow.Intent
import com.kishlaly.ta.openai.flow.Language
import com.kishlaly.ta.openai.flow.filterCSV
import com.kishlaly.ta.openai.flow.toFileName
import com.kishlaly.ta.openai.mainOutputFolder
import com.kishlaly.ta.openai.readCsv
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

val globalLanguage: Language = Language.DE
val globalBlogTopic = "Katzen"
var keywords = listOf<PAA>()

fun main() {

    // Отфильтровать дубликаты в csv
    // Загрузить контент
    // Сгенерить картинки
    // Преобразовать в WebP
    // Загрузить все картинки в блог
    // Создать XML

    val source = "katzenrassen"
    val domain = "katze101.com"
    val imageURI = "2023/03"
    filterCSV(source)
    keywords = readCSV(source)

    val total = keywords.size
    val processed = AtomicInteger(0)
    val xml = BlogpostXMLBuilder()
    val executor = Executors.newFixedThreadPool(5)

    keywords.take(1).forEach { paa ->
        val meta = BlogpostContentMeta(
            keyword = paa.title,
            domain = domain,
            imgURI = imageURI,
            imgSrcFolder = "openai/katze101/images_webp"
        )

//        executor.submit {
//            BlogpostDownloader(meta).downloadPAA()
//            processed.incrementAndGet()
//            println("==== Done $processed/$total ====")
//        }

        // ПЕРЕЛИНКОВКА !!!
        buildContent(xml, meta, paa, Intent.TAGS_PAA) {
            BlogpostContentBuilder(it).buildPAA()
        }
    }

    executor.shutdown()
    executor.awaitTermination(2, TimeUnit.HOURS)

    Files.write(Paths.get("$mainOutputFolder/posts.xml"), xml.build().toString().toByteArray())

}

private fun buildContent(
    xml: BlogpostXMLBuilder,
    meta: BlogpostContentMeta,
    paa: PAA,
    tagsIntent: Intent = Intent.TAGS,
    builder: (meta: BlogpostContentMeta) -> String
) {
    xml.append(meta, tagsIntent, builder)
    File("$mainOutputFolder/html/").mkdir()
    Files.write(
        Paths.get("$mainOutputFolder/html/${paa.title.toFileName()}.html"),
        htmlStub.replace("###content###", builder(meta)).toByteArray()
    )
    Files.write(
        Paths.get("$mainOutputFolder/html/raw_${paa.title.toFileName()}.raw"),
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