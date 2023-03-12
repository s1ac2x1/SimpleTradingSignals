package com.kishlaly.ta

import com.kishlaly.ta.openai.flow.Intent
import com.kishlaly.ta.openai.flow.createParagraphs
import com.kishlaly.ta.openai.mainOutputFolder
import java.io.File
import java.util.*

fun main() {
    val text = "Es gibt viele Gründe, warum Katzen kratzen, aber die wichtigsten sind:Erstens: Kratzen ist ein natürliches Verhalten für Katzen. Es hilft ihnen, Stress abzubauen und sich zu entspannen."
    val result = addSpaceAfterSymbol(text, ':')
    println(result)
}

fun addSpaceAfterSymbol(text: String, symbol: Char): String {
    var result = ""
    for (i in text.indices) {
        if (text[i] == symbol && i < text.length - 1 && text[i + 1] != ' ') {
            result += "${symbol} "
        } else {
            result += text[i]
        }
    }
    return result
}