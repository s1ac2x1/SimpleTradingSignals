package com.kishlaly.ta.openai.flow.blogpost

import com.kishlaly.ta.openai.KeywordSource
import com.kishlaly.ta.openai.flow.toFileName

enum class ArticleType {
    PAA,
    PAA_2,
    BIG,
    MEDIUM,
    SAVO
}

data class BlogpostContentMeta(
    val keywordSource: KeywordSource,
    val category: String,
    val type: ArticleType,
    val domain: String,
    val imgURI: String,
    val imgSrcFolder: String) {

    fun resolveKeywordFolder() = "openai/$domain/content/$category/${type.name.lowercase()}/${keywordSource.keyword.toFileName()}"

}