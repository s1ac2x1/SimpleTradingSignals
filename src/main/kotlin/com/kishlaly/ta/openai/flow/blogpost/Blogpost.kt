package com.kishlaly.ta.openai.flow.blogpost

import com.kishlaly.ta.openai.KeywordSource
import com.kishlaly.ta.openai.flow.Intent
import com.kishlaly.ta.openai.flow.Language
import com.kishlaly.ta.openai.flow.toFileName
import com.kishlaly.ta.openai.readCsv
import java.io.File
import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

var keywords = mapOf<ArticleType, List<KeywordSource>>()
val executor = Executors.newFixedThreadPool(20)
val processed = AtomicInteger(0)
val toBeProcessed = AtomicInteger(0)

var onlyOne = AtomicBoolean(false)
var onlyOneProcessed = AtomicBoolean(false)

// Перед созданием контента нового сайта
//    сгенерить картинки
//    перевести их в webp
//    сгенерить структуру папок (generateStructure)
//    обработать csv на предмет дублей и тд
//    запустить генерацию контента сразу по всем категориям

fun main() {
    val language = Language.DE
    val siteTopic = "Beziehungen"
    val imagesOnHosting = "2023/03"
    val domain = "beziehung101.com"
    val categories = listOf(
        "Kommunikation in Beziehungen",
        "Konfliktlösung in Beziehungen",
        "Bindungstheorie und Bindungsstile",
        "Intimität und Vertrauen",
        "Emotionale Intelligenz",
        "Grenzen setzen und einhalten",
        "Paartherapie und Eheberatung",
        "Elternschaft und Erziehung",
        "Sexualität und Beziehungen"
    )
    val types = listOf(ArticleType.PAA_2)

    //generateStructure(domain, categories, types)
    //onlyOne.set(true)
    //println(estimateCosts(domain, 0.11))

    // TODO прогонять еще раз в конце, чтобы подгрузилось то, что в первый раз не смогло по разным причинам
    categories.forEach { category ->
        types.forEach { type ->
            globalLanguage = language
            globalBlogTopic = siteTopic
            globalInsertImages = true
            globalInsertTags = true
            globalDomain = domain
            globalCategory = category.replace(" ", "-")
            globalImageURI = imagesOnHosting
            globalType = type

            download()
            //build(true)
        }
    }

    executor.shutdown()
    executor.awaitTermination(3, TimeUnit.HOURS)
}

private fun download() {
    keywords = readCSV()
    toBeProcessed.addAndGet(keywords[globalType]?.size ?: 0)
    keywords[globalType]
        ?.forEach { keywordSource ->
            val meta = BlogpostContentMeta(
                type = globalType,
                keywordSource = keywordSource,
                category = globalCategory,
                domain = globalDomain,
                imgURI = globalImageURI,
                imgSrcFolder = "openai/${globalDomain}/images_webp"
            )
            if (!onlyOne.get() || (onlyOne.get() && onlyOneProcessed.compareAndSet(false, true))) {
                executor.submit {
                    resolveDownloader(globalType)(meta)
                    processed.incrementAndGet()
                    println("==== Done $processed/${toBeProcessed.get()} ====\n")
                }
            }
        }
}

private fun build(saveTempHTML: Boolean) {
    val xml = BlogpostXMLBuilder()
    keywords = readCSV()
    toBeProcessed.addAndGet(keywords[globalType]?.size ?: 0)
    keywords[globalType]
        ?.forEach { keywordSource ->
            val meta = BlogpostContentMeta(
                type = globalType,
                keywordSource = keywordSource,
                category = globalCategory,
                domain = globalDomain,
                imgURI = globalImageURI,
                imgSrcFolder = "openai/${globalDomain}/images_webp"
            )
            buildContent(xml, meta, keywordSource, saveTempHTML)
        }
    Files.write(
        Paths.get("openai/$globalDomain/content/$globalCategory/${globalCategory}_${globalType.name.lowercase()}_posts.xml"),
        xml.build().toString().toByteArray()
    )
}

private fun estimateCosts(domain: String, costPerArticle: Double): String {
    var lines = 0
    findFiles(Paths.get("openai/$domain/content/"), "csv").forEach { path ->
        lines += path.toFile().readLines().size
    }
    return "$lines articles = $${lines * costPerArticle}"
}

fun findFiles(folder: Path, extension: String): List<Path> {
    val txtFiles = mutableListOf<Path>()

    Files.walkFileTree(folder, setOf(FileVisitOption.FOLLOW_LINKS), Int.MAX_VALUE, object : SimpleFileVisitor<Path>() {
        override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
            if (file.toString().endsWith(".${extension}")) {
                txtFiles.add(file)
            }
            return FileVisitResult.CONTINUE
        }

        override fun visitFileFailed(file: Path, exc: IOException): FileVisitResult {
            return FileVisitResult.CONTINUE
        }
    })

    return txtFiles
}

private fun generateStructure(domain: String, categories: List<String>, types: List<ArticleType>) {
    File("openai/$domain").mkdir()
    File("openai/$domain/content").mkdir()
    File("openai/$domain/temp").mkdir()
    File("openai/$domain/images").mkdir()
    File("openai/$domain/images_webp").mkdir()
    categories.forEach { cateroryRaw ->
        val category = cateroryRaw.replace(" ", "-")
        File("openai/$domain/content/$category").mkdir()
        types.forEach { type ->
            File("openai/$domain/content/$category/${type.name.lowercase()}").mkdir()
            File("openai/$domain/raw").listFiles().forEach { raw ->
                if (raw.name.lowercase().contains(category.lowercase())) {
                    val srcFile = "openai/$domain/content/$category/${category}_${type.name.lowercase()}.csv"
                    Files.copy(Paths.get(raw.absolutePath), Paths.get(srcFile), StandardCopyOption.REPLACE_EXISTING)
                    var src = File(srcFile).readLines()
                    if (src.isNotEmpty()) {
                        if (src[0].contains("PAA Title")) {
                            src = src.drop(1)
                        }
                        val formattedSrc = src
                            .map { it.replace(",", ";") }
                            .map { it.split(";")[0] }
                            .distinct()
                            .take(globalLimit)
                            .map { "${it};" }
                            .joinToString(System.lineSeparator())
                        File(srcFile).writeBytes(formattedSrc.toByteArray())
                    }
                }
            }
        }
    }
}

private fun setupGermanPAA(domain: String, caterogy: String, topic: String, imageURI: String) {
    globalLanguage = Language.DE
    globalBlogTopic = topic
    globalInsertImages = true
    globalInsertTags = true
    globalDomain = domain
    globalCategory = caterogy
    globalImageURI = imageURI
    globalType = ArticleType.PAA
}

private fun setupGermanBIG(domain: String, caterogy: String, topic: String, imageURI: String) {
    globalLanguage = Language.DE
    globalBlogTopic = topic
    globalInsertImages = true
    globalInsertTags = true
    globalDomain = domain
    globalCategory = caterogy
    globalImageURI = imageURI
    globalType = ArticleType.BIG
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
    println("Building ${meta.type} for [${keywordSource.keyword}]")
    val builder: (meta: BlogpostContentMeta) -> String = when (meta.type) {
        ArticleType.PAA -> { m -> BlogpostContentBuilder(m).buildPAA() }
        ArticleType.PAA_2 -> { m -> BlogpostContentBuilder(m).buildPAA2() }
        ArticleType.BIG -> { m -> BlogpostContentBuilder(m).buildLongPost() }
        ArticleType.MEDIUM -> { m -> BlogpostContentBuilder(m).buildMedium() }
        ArticleType.SAVO -> { m -> BlogpostContentBuilder(m).buildSavo() }
    }
    xml.append(meta, resolveTagsIntent(meta.type), builder)
    if (saveTempHTML) {
        File("openai/${meta.domain}/temp/").mkdir()
        Files.write(
            Paths.get("openai/${meta.domain}/temp/${keywordSource.keyword.toFileName()}.html"),
            htmlStub.replace("###content###", builder(meta)).toByteArray()
        )
    }
}

fun resolveTagsIntent(type: ArticleType) = when (type) {
    ArticleType.PAA -> Intent.TAGS_PAA
    ArticleType.PAA_2 -> Intent.TAGS
    ArticleType.BIG -> Intent.TAGS
    ArticleType.MEDIUM -> Intent.TAGS
    ArticleType.SAVO -> Intent.TAGS
}

fun resolveDownloader(type: ArticleType): (BlogpostContentMeta) -> Unit {
    return when (type) {
        ArticleType.PAA -> { meta -> run { BlogpostDownloader(meta).downloadPAA() } }
        ArticleType.PAA_2 -> { meta -> run { BlogpostDownloader(meta).downloadPAA2() } }
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
            val keywords =
                readCsv("openai/$globalDomain/content/$globalCategory/${globalCategory}_${type.name.lowercase()}.csv")
                    .distinctBy { it.keyword }
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
var globalLimit = 1000
var globalImageURI = ""
var globalType = ArticleType.MEDIUM
var globalInterlinkage = false
var globalUseTone = true
