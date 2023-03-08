package com.kishlaly.ta

import com.kishlaly.ta.openai.mainOutputFolder
import java.io.File
import kotlin.random.Random

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

    val tone = listOf("Objektiv", "Subjektiv", "Beschreibend", "Informativ", "Unterhaltsam", "Lyrisch", "Humorvoll", "Persönlich", "Dramatisch", "Kritisch")
    println(tone[Random.nextInt(tone.size)])
}
