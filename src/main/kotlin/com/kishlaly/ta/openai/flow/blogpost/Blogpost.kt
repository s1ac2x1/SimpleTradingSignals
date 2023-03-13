package com.kishlaly.ta.openai.flow.blogpost

import com.kishlaly.ta.openai.KeywordSource
import com.kishlaly.ta.openai.flow.Intent
import com.kishlaly.ta.openai.flow.Language
import com.kishlaly.ta.openai.flow.toFileName
import com.kishlaly.ta.openai.readCsv
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

val globalLanguage: Language = Language.DE
val globalBlogTopic = "Katzen"
var keywords = listOf<KeywordSource>()

fun main() {

    // Отфильтровать дубликаты в csv
    // Загрузить контент
    // Сгенерить картинки
    // Преобразовать в WebP
    // Загрузить все картинки в блог
    // Создать XML

    val domain = "katze101.com"
    val category = "katzenverhalten"
    val imageURI = "2023/03"

    // run only once per new category
    //filterCSV(domain, source, 250)

    keywords = readCSV(domain, category)

    val total = keywords.size
    val processed = AtomicInteger(0)
    val xml = BlogpostXMLBuilder()
    val executor = Executors.newFixedThreadPool(5)

    keywords.take(1).forEach { keywordSource ->
        val meta = BlogpostContentMeta(
            type = ArticleType.PAA,
            keyword = keywordSource.title,
            category = category,
            domain = domain,
            imgURI = imageURI,
            imgSrcFolder = "openai/${domain}/images_webp"
        )

//        executor.submit {
//            BlogpostDownloader(meta).downloadPAA()
//            processed.incrementAndGet()
//            println("==== Done $processed/$total ====\n")
//        }

        // нужна еще перелинковка для больших статей
        buildContent(xml, meta, keywordSource, Intent.TAGS_PAA) {
            BlogpostContentBuilder(it).buildPAA()
        }
    }

    executor.shutdown()
    executor.awaitTermination(2, TimeUnit.HOURS)

    Files.write(Paths.get("openai/$domain/content/$category/${category}_posts.xml"), xml.build().toString().toByteArray())

}

private fun buildContent(
    xml: BlogpostXMLBuilder,
    meta: BlogpostContentMeta,
    keywordSource: KeywordSource,
    tagsIntent: Intent = Intent.TAGS,
    builder: (meta: BlogpostContentMeta) -> String
) {
    xml.append(meta, tagsIntent, builder)
    Files.write(
        Paths.get("openai/${meta.domain}/temp/${keywordSource.title.toFileName()}.html"),
        htmlStub.replace("###content###", builder(meta)).toByteArray()
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

fun readCSV(domain: String, category: String): List<KeywordSource> {
    return try {
        readCsv("openai/$domain/content/$category/$category.csv")
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