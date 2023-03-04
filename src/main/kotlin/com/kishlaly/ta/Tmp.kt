package com.kishlaly.ta

import com.kishlaly.ta.openai.mainOutputFolder
import java.io.File

val text = """
    Es gibt keine einzelne Farbe, die Katzen aggressiv macht. Tatsächlich können verschiedene Faktoren, einschließlich Farbe, die Aggressivität einer Katze beeinflussen. Einige Beispiele sind:

    – Umgebung: Eine Katze kann aggressiv werden, wenn sie sich in einer ungewohnten Umgebung befindet, in der sie sich unsicher fühlt. Dies kann durch eine neue Farbe oder ein neues Muster in der Umgebung verursacht werden.

    – Farbkontrast: Katzen reagieren auf Farbkontraste. Wenn eine Katze eine helle Farbe auf einem dunklen Hintergrund sieht, kann sie sich bedroht fühlen und aggressiv werden.

    – Farbkombinationen: Katzen reagieren auch auf Farbkombinationen. Einige Farbkombinationen, wie Rot und Schwarz, können Aggressionen auslösen.

    – Farbintensität: Eine Katze kann auch aggressiv werden, wenn sie eine sehr intensive Farbe sieht. Dies kann durch ein helles Rot oder Gelb verursacht werden.

    Insgesamt kann man also sagen, dass es keine einzelne Farbe gibt, die Katzen aggressiv macht. Es können viele verschiedene Faktoren, einschließlich Farbe, die Aggressivität einer Katze beeinflussen.
""".trimIndent()

fun main() {
//    val screen2 = CacheReader.getSymbolData(TaskType.THREE_DISPLAYS_BUY.getTimeframeIndicators(2), "LUMN")
//    println(screen2.lastQuote.nativeDate)
//    val efi: List<ElderForceIndex> = screen2.indicators.get(Indicator.EFI) as List<ElderForceIndex>
//    println(efi)

//    val listFiles = File("openai/output/images").listFiles()
//    File("openai/output/text/").listFiles().forEach {
//        val text = it.readText()
//        val imageUrl = listFiles.random().name
//        val corrected = text.replace(".DS_Store", imageUrl)
//        it.writeText(corrected)
//    }

//    val content = File("openai/katzenverhalten.xml").readText()
//    val newContent = content.replace("<content>.\n\n", "<content>")
//    File("openai/katzenverhalten2.xml").writeText(newContent)

    val text = "- Unterhaltung: Beschäftigen Sie Ihre Katze mit Spielzeug oder anderen Beschäftigungsmöglichkeiten"
    var shorter = text
    if (text.indexOf(':') > 0) {
        shorter = text.substring(0, text.indexOf(':'))
    }
    if (text.indexOf(';') > 0) {
        shorter = shorter.substring(0, shorter.indexOf(':'))
    }
    if (text.indexOf('-') in 0..3 ) {
        shorter = shorter.substring(shorter.indexOf('-') + 1, shorter.length)
    }
    println(shorter)
}