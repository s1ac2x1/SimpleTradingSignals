package com.kishlaly.ta.openai.flow

import com.kishlaly.ta.openai.filenameRegex
import com.kishlaly.ta.openai.flow.blogpost.BlogpostContentBuilder
import com.kishlaly.ta.openai.flow.blogpost.BlogpostContentMeta
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
        val pattern = filenameRegex.replace(meta.keyword, "_")
        val featuredImageURL =
            File("$mainOutputFolder/$pattern").listFiles().find { it.name.contains(pattern) }?.name ?: ""
        xml.append(featuredImageURL)
        xml.append("</picture>")

        xml.append("</post>")

        return this
    }

    fun build(): String {
        xml.append("</output>")
        return xml.toString()
    }
}