package com.kishlaly.ta.openai.flow.blogpost

import com.kishlaly.ta.openai.filenameRegex
import com.kishlaly.ta.openai.flow.Intent
import com.kishlaly.ta.openai.flow.toFileName
import com.kishlaly.ta.openai.mainOutputFolder
import java.io.File

class BlogpostXMLBuilder() {

    private val xml = StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?><output>")

    fun append(meta: BlogpostContentMeta): BlogpostXMLBuilder {
        xml.append("<post>")

        xml.append("<title>")
        xml.append(meta.keyword)
        xml.append("</title>")

        xml.append("<content>")
        xml.append(BlogpostContentBuilder(meta).build())
        xml.append("</content>")

        xml.append("<featuredImage>")
        val postFolder = meta.keyword.toFileName()
        val featuredImageURL =
            File("$mainOutputFolder/$postFolder").listFiles().find { it.name.contains(postFolder) }?.name ?: ""
        xml.append(featuredImageURL)
        xml.append("</featuredImage>")

        xml.append("<tags>")
        val tags = File("$mainOutputFolder/$postFolder/${Intent.TAGS}_1").readText()
        xml.append(tags)
        xml.append("</tags>")

        xml.append("</post>")

        return this
    }

    fun build(): String {
        xml.append("</output>")
        return xml.toString()
    }
}