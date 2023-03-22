package com.kishlaly.ta.openai.flow.blogpost

import com.kishlaly.ta.openai.KeywordSource
import com.kishlaly.ta.openai.flow.Intent
import com.kishlaly.ta.openai.flow.Language
import com.kishlaly.ta.openai.flow.firstFilterCSV
import com.kishlaly.ta.openai.flow.toFileName
import com.kishlaly.ta.openai.readCsv
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

var keywords = mapOf<ArticleType, List<KeywordSource>>()

fun main() {
    setupGermanPAA("katze101.com", "katzenfutter-und-ernährung", "Katzen", "2023/03")
    //setupMedium("cats")

    firstFilterCSV()
    keywords = readCSV()
    val total = keywords[globalType]?.size ?: 0
    val processed = AtomicInteger(0)
    val xml = BlogpostXMLBuilder()
    val executor = Executors.newFixedThreadPool(5)

    keywords[globalType]
        //?.shuffled()
        //?.take(1)
        ?.forEach { keywordSource ->
        val meta = BlogpostContentMeta(
            type = globalType,
            keyword = keywordSource.title,
            category = globalCategory,
            domain = globalDomain,
            imgURI = globalImageURI,
            imgSrcFolder = "openai/${globalDomain}/images_webp"
        )

            // если часть вопросов переделать в утвердительные? и где-то можно добавить цифры
            // external links? наверно в конце парочку нужно

            // добавлять абзац в начале про рекламу для Саво

//        executor.submit {
//            resolveDownloader(globalType)(meta)
//            processed.incrementAndGet()
//            println("==== Done $processed/$total ====\n")
//        }

            // перелинковка плагином? тогда можно шедулить на будущее?
       buildContent(xml, meta, keywordSource, false)
    }

    executor.shutdown()
    executor.awaitTermination(2, TimeUnit.HOURS)

    Files.write(Paths.get("openai/$globalDomain/content/$globalCategory/${globalCategory}_${globalType.name.lowercase()}_posts.xml"), xml.build().toString().toByteArray())

}

private fun setupGermanPAA(domain: String, caterogy: String, topic: String, imageURI: String) {
    globalLanguage = Language.DE
    globalBlogTopic = topic
    globalInsertImages = true
    globalInsertTags = true
    globalDomain = domain
    globalCategory = caterogy
    globalLimit = 500
    globalImageURI = imageURI
    globalType = ArticleType.PAA
    globalInterlinkage = true
}

private fun setupGermanBIG(domain: String, caterogy: String, topic: String, imageURI: String) {
    globalLanguage = Language.DE
    globalBlogTopic = topic
    globalInsertImages = true
    globalInsertTags = true
    globalDomain = domain
    globalCategory = caterogy
    globalLimit = 500
    globalImageURI = imageURI
    globalType = ArticleType.BIG
    globalInterlinkage = true
}

private fun setupMedium(topic: String) {
    globalLanguage = Language.EN
    globalBlogTopic = topic
    globalCategory = "main"
    globalDomain = "medium"
    globalType = ArticleType.MEDIUM
}

fun buildContent(
    xml: BlogpostXMLBuilder,
    meta: BlogpostContentMeta,
    keywordSource: KeywordSource,
    saveTempHTML: Boolean
) {
    println("Building ${meta.type} for [${keywordSource.title}]")
    val builder: (meta: BlogpostContentMeta) -> String = when (meta.type) {
        ArticleType.PAA -> { m -> BlogpostContentBuilder(m).buildPAA() }
        ArticleType.BIG -> { m -> BlogpostContentBuilder(m).buildLongPost() }
        ArticleType.MEDIUM -> { m -> BlogpostContentBuilder(m).buildMedium() }
        ArticleType.SAVO -> { m -> BlogpostContentBuilder(m).buildSavo() }
    }
    xml.append(meta, resolveTagsIntent(meta.type), builder)
    if (saveTempHTML) {
    Files.write(
        Paths.get("openai/${meta.domain}/temp/${keywordSource.title.toFileName()}.html"),
        htmlStub.replace("###content###", builder(meta)).toByteArray()
    )
    }
}

fun resolveTagsIntent(type: ArticleType) = when (type) {
    ArticleType.PAA -> Intent.TAGS_PAA
    ArticleType.BIG -> Intent.TAGS
    ArticleType.MEDIUM -> Intent.TAGS
    ArticleType.SAVO -> Intent.TAGS
}

fun resolveDownloader(type: ArticleType): (BlogpostContentMeta) -> Unit {
    return when (type) {
        ArticleType.PAA -> { meta -> run { BlogpostDownloader(meta).downloadPAA() } }
        ArticleType.BIG -> { meta -> run { BlogpostDownloader(meta).downloadBigPost() } }
        ArticleType.MEDIUM -> { meta -> run { BlogpostDownloader(meta).downloadMedium() } }
        ArticleType.SAVO -> { meta -> run { BlogpostDownloader(meta).downloadSavo() } }
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

fun readCSV(): MutableMap<ArticleType, List<KeywordSource>> {
    val result = mutableMapOf<ArticleType, List<KeywordSource>>()
        ArticleType.values().forEach { type ->
            try {
                val keywords = readCsv("openai/$globalDomain/content/$globalCategory/${globalCategory}_${type.name.lowercase()}.csv")
                    .distinctBy { it.title }
                    .toList()
                result.put(type, keywords)
            } catch (e: Exception) {
                println("")
            }
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

var globalLanguage = Language.EN
var globalBlogTopic = ""
var globalInsertImages = false
var globalInsertTags = false
var globalDomain = ""
var globalCategory = ""
var globalLimit = 350
var globalImageURI = ""
var globalType = ArticleType.MEDIUM
var globalInterlinkage = false
