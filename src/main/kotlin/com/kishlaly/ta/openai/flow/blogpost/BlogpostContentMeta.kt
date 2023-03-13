package com.kishlaly.ta.openai.flow.blogpost

data class BlogpostContentMeta(
    val keyword: String,
    val category: String,
    val domain: String,
    val imgURI: String,
    val imgSrcFolder: String) {
}