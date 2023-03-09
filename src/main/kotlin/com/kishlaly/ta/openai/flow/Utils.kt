package com.kishlaly.ta.openai.flow

import com.kishlaly.ta.openai.filenameRegex
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import kotlin.random.Random

fun timeoutRetry(
    client: OkHttpClient,
    request: Request,
    retries: Int = 3
): String? {
    var response: Response? = null
    var attempts = 0

    while (response == null && attempts < retries) {
        try {
            response = client.newCall(request).execute()
        } catch (e: IOException) {
            attempts++
            println("Attempt $attempts failed. Retrying in 10 seconds")
            Thread.sleep(10 * 1000)
        }
    }

    return response?.body?.string()
}

fun String.toFileName() = filenameRegex.replace(this, "_")

val lineBreaksRegex = Regex("\n")
val contentRegex = Regex("\n\n")
val finalRegex = Regex("\n\n\n")
val numericListRegex = Regex("\\d+\\. ")

val trimmed: (String) -> String = { it.trim() }
val removeAllLineBreaks: (String) -> String = { lineBreaksRegex.replace(it, "") }
val removeQuotes: (String) -> String = { it.replace("\"", "") }
val removeDots: (String) -> String = { it.replace(".", "") }
val removeNumericList: (String) -> String = { numericListRegex.replace(it, "") }
val removeQuestionMarks: (String) -> String = { it.replace("?", "") }
val filterBadTOC: (String) -> String = {
    val correctedToc = it.lines()
        .map(trimmed)
        .filter { it.length >= 10 }
        .filter { it.length <= 100 }
        .filter { it[0].isLetter() }
        .filter { it[0].isUpperCase() }
        .joinToString("\n")
    correctedToc
}
val resolveShortKeyword: (String) -> String = {
    var shorter = it
    if (it.indexOf(':') > 0) {
        shorter = it.substring(0, it.indexOf(':'))
    }
    if (shorter.indexOf(';') > 0) {
        shorter = shorter.substring(0, shorter.indexOf(':'))
    }
    if (shorter.indexOf('-') in 0..3) {
        shorter = shorter.substring(shorter.indexOf('-') + 1, shorter.length)
    }
    shorter
}
val createParagraphs: (String) -> String = {
    val output = StringBuilder()

    it.split(". ")
        .filter { !it.isNullOrBlank() }
        .map { it.trim() }
        .filter { it.length > 10 }
        .chunked(Random.nextInt(2, 4))
        .forEach { chunk ->
            output.append("<p>")
            chunk.forEach { output.append(it).append(". ") }
            output.append("</p>")
        }

    output.toString()
}
val removeFirstSentence: (String) -> String = { str ->
    str.substring(str.indexOfFirst { it == '.' } + 1, str.length)
}