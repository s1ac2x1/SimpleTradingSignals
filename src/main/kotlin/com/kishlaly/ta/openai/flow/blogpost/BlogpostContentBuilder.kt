package com.kishlaly.ta.openai.flow.blogpost

import com.kishlaly.ta.openai.flow.Intent
import com.kishlaly.ta.openai.flow.finalRegex
import com.kishlaly.ta.openai.flow.toFileName
import com.kishlaly.ta.openai.mainOutputFolder
import java.io.File

class BlogpostContentBuilder(val meta: BlogpostContentMeta) {

    fun build(): String {
        val srcFolder = "$mainOutputFolder/${meta.keyword.toFileName()}"

        if (!File("$srcFolder").exists()) {
            throw RuntimeException("Nothing to build, $srcFolder doesn't exist")
        }

        val introduction = File("$srcFolder/${Intent.INTRODUCTION}_1").readText()
        val tocPlan = File("$srcFolder/${Intent.TOC_PLAN}_1").readLines()

        val tocContent = StringBuilder()
        tocPlan.forEachIndexed { index, item ->
            tocContent.append("<h2>$item</h2>")

            val imageName =
                File("$srcFolder").listFiles()
                    .filter { it.name.contains("${Intent.TOC_IMAGES}") }.firstOrNull()?.name ?: ""
            if (imageName.isNotEmpty()) {
                var imageURL = "https://$${meta.domain}/wp-content/uploads/${meta.imgURI}/$imageName"
                tocContent.append("<img src='$imageURL'></img>")
            }

            // TODO тут нужно склеить все три части, разбить на параграфы по 1-3 предложения и обернуть <p></p>
            val content = StringBuilder()
            listOf(Intent.CONTENT_PART_1_HISTORY, Intent.CONTENT_PART_2_MAIN, Intent.CONTENT_PART_3_FACTS).shuffled().forEach {
                val part =
                    File("$srcFolder").listFiles().find { it.name.contains("${Intent.CONTENT_PART_1_HISTORY}_${index + 1}") }
                        ?.readText() ?: ""
                content.append(part)
                //tocContent.append("<p>$part</p>")
            }
        }

        val oppositeOpitionSubtitle = File("$srcFolder/${Intent.OPPOSITE_OPINION_QUESTION}_1").readText()
        val oppositeOpinionText = File("$srcFolder/${Intent.OPPOSITE_OPINION_TEXT}_1").readText()

        val conclusion = File("$srcFolder/${Intent.CONCLUSION}_1").readText()
        val randomAddition = File("$srcFolder/${Intent.RANDOM_ADDITION}_1").readText()

        var content = """
        <p>$introduction</p>
        $tocContent
        <h2>$oppositeOpitionSubtitle</h2>
        <p>$oppositeOpinionText</p>
        <p>$conclusion</p>
        <p>$randomAddition</p>
    """.trimIndent()

        content = finalRegex.replace(content, "")
        content = content.replace("!.", ".")
        content = content.replace("!", ".")
        content = content.replace(". ,", ".,")
        content = content.replace("  ", " ")

        return content
    }

}