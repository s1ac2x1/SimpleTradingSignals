package com.kishlaly.ta.openai.flow.blogpost

enum class ArticleType {
    PAA,
    BIG
}

data class BlogpostContentMeta(
    val keyword: String,
    val category: String,
    val type: ArticleType,
    val domain: String,
    val imgURI: String,
    val imgSrcFolder: String) {
}