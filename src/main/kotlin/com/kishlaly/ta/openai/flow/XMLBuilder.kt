package com.kishlaly.ta.openai.flow

import com.kishlaly.ta.openai.filenameRegex
import com.kishlaly.ta.openai.flow.blogpost.BlogpostContentBuilder
import com.kishlaly.ta.openai.flow.blogpost.BlogpostContentMeta
import com.kishlaly.ta.openai.mainOutputFolder
import java.io.File

class XMLBuilder() {

    private val xml = StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?><output>")

    fun append(meta: BlogpostContentMeta): XMLBuilder {
        xml.append("<post>")

        xml.append("<title>")
        xml.append(meta.keyword)
        xml.append("</title>")

        xml.append("<content>")
        xml.append(BlogpostContentBuilder(meta).build())
        xml.append("</content>")

        xml.append("<picture>")
        val postFolder = filenameRegex.replace(meta.keyword, "_")
        val featuredImageURL =
            File("$mainOutputFolder/$postFolder").listFiles().find { it.name.contains(postFolder) }?.name ?: ""
        xml.append(featuredImageURL)
        xml.append("</picture>")

        xml.append("<tags>")
        val tags = File("$mainOutputFolder/$postFolder/step_8_1").readText()
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