package com.kishlaly.ta.openai.flow.blogpost

import com.kishlaly.ta.openai.flow.toFileName

enum class ArticleType {
    PAA,
    BIG,
    MEDIUM
}

data class BlogpostContentMeta(
    val keyword: String,
    val category: String,
    val type: ArticleType,
    val domain: String,
    val imgURI: String,
    val imgSrcFolder: String) {

    fun resolveKeywordFolder() = "openai/$domain/content/$category/${type.name.lowercase()}/${keyword.toFileName()}"

}