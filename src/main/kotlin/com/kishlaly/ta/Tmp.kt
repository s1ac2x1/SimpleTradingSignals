package com.kishlaly.ta

import com.kishlaly.ta.openai.flow.encodeURL

fun main() {
    val text = "Ist es eine Quälerei Katzen in der Wohnung zu halten"
    val result = encodeURL(text)
    println(result)
}