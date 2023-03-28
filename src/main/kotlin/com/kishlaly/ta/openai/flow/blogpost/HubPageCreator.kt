package com.kishlaly.ta.openai.flow.blogpost

import com.kishlaly.ta.openai.flow.encodeURL
import java.io.File
import java.nio.charset.StandardCharsets

fun main() {
    val domain = "hund101.com"
    val category = "Hundebezogene-Reisen"
    val links = StringBuilder()
    File("openai/$domain/content/$category/${category}_paa.csv")
        .readLines()
        .map { it.replace(";", "") }
        .forEach { title ->
            links.append("<a href=\"https://${domain}/${title.replace("?", "").encodeURL()}/\">${title}</a><br>")
        }
    File("$category.txt").writeText(links.toString().replace("\uFEFF", ""), StandardCharsets.UTF_8)
}