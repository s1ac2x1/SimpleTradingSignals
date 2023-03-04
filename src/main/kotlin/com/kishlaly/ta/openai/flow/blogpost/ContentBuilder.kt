package com.kishlaly.ta.openai.flow.blogpost

import com.kishlaly.ta.openai.filenameRegex
import com.kishlaly.ta.openai.finalRegex
import com.kishlaly.ta.openai.mainOutputFolder
import java.io.File

class ContentBuilder(val meta: BlogpostContentMeta) {

    fun build(): String {
        val srcFolder = "$mainOutputFolder/${filenameRegex.replace(meta.keyword, "_")}"

        if (!File("$srcFolder").exists()) {
            throw RuntimeException("Nothing to build, $srcFolder doesn't exist")
        }

        val introduction = File("$srcFolder/step_1_1").readText()
        val tocPlan = File("$srcFolder/step_2_1").readLines()

        val tocContent = StringBuilder()
        tocPlan.forEachIndexed { index, item ->
            tocContent.append("<h2>$item</h2>")

            val imageName =
                File("$srcFolder").listFiles()
                    .find { it.name.contains(filenameRegex.replace(item, "_")) }?.name
                    ?: ""
            var imageURL = "https://$${meta.domain}/wp-content/uploads/${meta.imgURI}/$imageName"
            tocContent.append("<img src='$imageURL'></img>")

            val content_step_3 =
                File("$srcFolder").listFiles().find { it.name.contains("step_3_${index + 1}") }
                    ?.readText() ?: ""
            val content_step_4 =
                File("$srcFolder").listFiles().find { it.name.contains("step_4_${index + 1}") }
                    ?.readText() ?: ""
            val content_step_5 =
                File("$srcFolder").listFiles().find { it.name.contains("step_5_${index + 1}") }
                    ?.readText() ?: ""

            tocContent.append("<p>$content_step_3</p>")
            tocContent.append("<p>$content_step_4</p>")
            tocContent.append("<p>$content_step_5</p>")
        }

        val oppositeOpitionSubtitle = File("$srcFolder/step_6_1").readText()
        val oppositeOpinionText = File("$srcFolder/step_7_1").readText()

        val conclusion = File("$srcFolder/step_10_1").readText()
        val randomAddition = File("$srcFolder/step_11_1").readText()

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

        return content
    }

}