package com.kishlaly.ta.openai.flow.blogpost

import com.kishlaly.ta.openai.KeywordSource
import com.kishlaly.ta.openai.flow.Intent
import com.kishlaly.ta.openai.flow.Language
import com.kishlaly.ta.openai.flow.filterCSV
import com.kishlaly.ta.openai.flow.toFileName
import com.kishlaly.ta.openai.readCsv
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

// TODO всегда проверять все эти настройки ниже:
val globalLanguage: Language = Language.DE
val globalBlogTopic = "Katzen"
val insertImages = true
val domain = "katze101.com"
val category = "gesundheit-von-katzen"
val limit = 300
val imageURI = "2023/03"
val type = ArticleType.PAA
val interlinkage = true

var keywords = mapOf<ArticleType, List<KeywordSource>>()

fun main() {

    // Отфильтровать дубликаты в csv
    // Загрузить контент
    // Сгенерить картинки
    // Преобразовать в WebP
    // Загрузить все картинки в блог
    // Создать XML

    // run only once per new category
    filterCSV()

    keywords = readCSV(domain, category)

    val total = keywords.size
    val processed = AtomicInteger(0)
    val xml = BlogpostXMLBuilder()
    val executor = Executors.newFixedThreadPool(10)

    keywords[type]
        //.take(1)
        ?.forEach { keywordSource ->
        val meta = BlogpostContentMeta(
            type = type,
            keyword = keywordSource.title,
            category = category,
            domain = domain,
            imgURI = imageURI,
            imgSrcFolder = "openai/${domain}/images_webp"
        )

//        executor.submit {
//            resolveDownloader(type)(meta)
//            processed.incrementAndGet()
//            println("==== Done $processed/$total ====\n")
//        }

        // нужна еще перелинковка для больших статей
//       buildContent(xml, meta, keywordSource)
    }

    executor.shutdown()
    executor.awaitTermination(2, TimeUnit.HOURS)

//    Files.write(Paths.get("openai/$domain/content/$category/${category}_${type.name.lowercase()}_posts.xml"), xml.build().toString().toByteArray())

}

private fun buildContent(
    xml: BlogpostXMLBuilder,
    meta: BlogpostContentMeta,
    keywordSource: KeywordSource
) {
    println("Building ${meta.type} for [${keywordSource.title}]")
    val builder: (meta: BlogpostContentMeta) -> String = when (meta.type) {
        ArticleType.PAA -> { m -> BlogpostContentBuilder(m).buildPAA() }
        ArticleType.BIG -> { m -> BlogpostContentBuilder(m).buildLongPost() }
    }
    xml.append(meta, resolveTagsIntent(meta.type), builder)
    Files.write(
        Paths.get("openai/${meta.domain}/temp/${keywordSource.title.toFileName()}.html"),
        htmlStub.replace("###content###", builder(meta)).toByteArray()
    )
}

fun resolveTagsIntent(type: ArticleType) = when (type) {
    ArticleType.PAA -> Intent.TAGS_PAA
    ArticleType.BIG -> Intent.TAGS
}

fun resolveDownloader(type: ArticleType): (BlogpostContentMeta) -> Unit {
    return when (type) {
        ArticleType.PAA -> { meta -> run { BlogpostDownloader(meta).downloadPAA() } }
        ArticleType.BIG -> { meta -> run { BlogpostDownloader(meta).downloadBigPost() } }
    }
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

fun readCSV(domain: String, category: String): MutableMap<ArticleType, List<KeywordSource>> {
    val result = mutableMapOf<ArticleType, List<KeywordSource>>()
    try {
        ArticleType.values().forEach { type ->
            val keywords = readCsv("openai/$domain/content/$category/${category}_${type.name.lowercase()}.csv")
                .distinctBy { it.title }
                .toList()
            result.put(type, keywords)
        }
    } catch (e: Exception) {
        // ignore
    }
    return result
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