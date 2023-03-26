package com.kishlaly.ta.openai.flow.blogpost

import com.kishlaly.ta.openai.flow.encodeURL
import java.io.File
import java.nio.charset.StandardCharsets

fun main() {
    val domain = "katze101.com"
    val category = "katzenrassen"
    val links = StringBuilder()
    File("openai/$domain/content/$category/${category}_paa.csv")
        .readLines()
        .map { it.replace(";", "") }
        .forEach { title ->
            links.append("<a href=\"https://${domain}/${title.replace("?", "").encodeURL()}/\">${title}</a><br>")
        }
    File("$category.txt").writeText(links.toString().replace("\uFEFF", ""), StandardCharsets.UTF_8)
}