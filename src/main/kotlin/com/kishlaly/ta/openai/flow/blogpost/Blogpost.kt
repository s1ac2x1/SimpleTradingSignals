package com.kishlaly.ta.openai.flow.blogpost

fun main() {
    val meta = BlogpostContentMeta(
        keyword = "Welche Katzen Haaren am wenigsten?",
        domain = "katze101.com",
        imgURI = "2023/03"
    )
    BlogpostDownloader(meta).download()
    // залить картинки на хостиниг перед следующим шагом
//    val xml = XMLBuilder().append(meta).build()
//    Files.write(Paths.get("$mainOutputFolder/posts.xml"), xml.toString().toByteArray())
}
