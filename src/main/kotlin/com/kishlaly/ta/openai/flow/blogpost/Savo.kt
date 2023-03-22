package com.kishlaly.ta.openai.flow.blogpost

import com.github.doyaaaaaken.kotlincsv.client.CsvReader
import com.kishlaly.ta.openai.KeywordSource
import com.kishlaly.ta.openai.flow.Language
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
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
                    keywordSource = keywordSource,
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
//               buildContent(xml, meta, keywordSource, true)
          }

     executor.shutdown()
     executor.awaitTermination(2, TimeUnit.HOURS)

}

fun parseCsvFileWithLibrary(fileName: String): List<List<String>> {
     val result: MutableList<List<String>> = mutableListOf()
     CsvReader().open(fileName) {
          readAllWithHeader().forEach { row ->
               val values = row.values.map { it.toString() }
               result.add(values)
          }
     }
     return result
}

fun parseKeywords(): MutableMap<ArticleType, List<KeywordSource>> {
     val result = mutableMapOf<ArticleType, List<KeywordSource>>()
     ArticleType.values().forEach { type ->
          try {
               val keywords = parseCsvFileWithLibrary("openai/$globalDomain/content/$globalCategory/${globalCategory}_${type.name.lowercase()}.csv")
                    .map { list ->
                         val title = list[0]
                         val prompt = if (list.size > 2) list[2] else ""
                         KeywordSource(title, prompt)
                    }.distinctBy { it.keyword }.toList()
               result.put(type, keywords)
          } catch (e: Exception) {
               println("")
          }
     }
     return result
}

