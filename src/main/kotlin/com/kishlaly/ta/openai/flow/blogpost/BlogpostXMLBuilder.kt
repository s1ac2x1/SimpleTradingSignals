package com.kishlaly.ta.openai.flow.blogpost

import com.kishlaly.ta.openai.flow.Intent
import java.io.File

class BlogpostXMLBuilder() {

    private val xml = StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?><output>")

    fun append(
        meta: BlogpostContentMeta,
        tagsIntent: Intent = Intent.TAGS,
        content: (meta: BlogpostContentMeta) -> String
    ): BlogpostXMLBuilder {
        xml.append("<post>")

        xml.append("<title>")
        xml.append(meta.keyword)
        xml.append("</title>")

        xml.append("<content><![CDATA[")
        xml.append(content(meta))
        xml.append("]]></content>")

        xml.append("<featuredImage>")
        val postFolder = meta.resolveKeywordFolder()
        var featuredImageURL = File(meta.imgSrcFolder).listFiles().random().name
        xml.append("https://${meta.domain}/wp-content/uploads/${meta.imgURI}/$featuredImageURL")
        xml.append("</featuredImage>")

        xml.append("<tags>")
        val tags = File("$postFolder/${tagsIntent}_1").readText()
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