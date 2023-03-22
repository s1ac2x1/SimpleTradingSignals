package com.kishlaly.ta.openai.flow.blogpost

import com.kishlaly.ta.openai.DELIMITER
import com.kishlaly.ta.openai.KeywordSource
import com.kishlaly.ta.openai.flow.Language
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger


fun main() {
     globalLanguage = Language.EN
     globalBlogTopic = "hosting"
     globalInsertImages = false
     globalInsertTags = false
     globalDomain = "savo"
     globalCategory = "hosting"
     globalLimit = 350
     globalImageURI = ""
     globalType = ArticleType.SAVO
     globalInterlinkage = false

     keywords = parseKeywords()

     val total = keywords[globalType]?.size ?: 0
     val processed = AtomicInteger(0)
     val xml = BlogpostXMLBuilder()
     val executor = Executors.newFixedThreadPool(5)

     keywords[globalType]
          //?.shuffled()
          ?.take(1)
          ?.forEach { keywordSource ->
               val meta = BlogpostContentMeta(
                    type = globalType,
                    keyword = keywordSource.title,
                    category = globalCategory,
                    domain = globalDomain,
                    imgURI = globalImageURI,
                    imgSrcFolder = "openai/${globalDomain}/images_webp"
               )

               // добавлять абзац в начале про рекламу для Саво

        executor.submit {
            resolveDownloader(globalType)(meta)
            processed.incrementAndGet()
            println("==== Done $processed/$total ====\n")
        }

               // перелинковка плагином? тогда можно шедулить на будущее?
//               buildContent(xml, meta, keywordSource, false)
          }

}

fun parseKeywords(): MutableMap<ArticleType, List<KeywordSource>> {
     val result = mutableMapOf<ArticleType, List<KeywordSource>>()
     ArticleType.values().forEach { type ->
          try {
               val keywords = File("openai/$globalDomain/content/$globalCategory/${globalCategory}_${type.name.lowercase()}.csv").readLines().map { line ->
                    val split = line.split(DELIMITER)
                    val title = split[0]
                    val prompt = if (split.size > 2) split[2] else ""
                    KeywordSource(title, prompt)
               }.distinctBy { it.title }.toList()
               result.put(type, keywords)
          } catch (e: Exception) {
               println("")
          }
     }
     return result
}

