package com.kishlaly.ta

import com.kishlaly.ta.openai.mainOutputFolder
import java.io.File

val text = """
, die Ihnen zu diesem Thema einfallen.

Warum kratzen Katzen 
Wie verhindert man das Kratzen 
Kratzpfosten als Alternative 
Richtige Pflege der Krallen 
Bedeutung des Kratzens für Katzen 
Was sind die Gründe für das Kratzen 
Die richtige Ernährung der Katze 
Spielsachen als Ablenkung vom Kratzen  
Geeignete Umgebung für Katzen schaffen  
Alternativen zu körperlichem Stress bieten  
Den richtigen Zeitpunkt wählen, um eine Katze zu streicheln  
Ausreichend Beschäftigung und Aufmerksamkeit geben  
Vermeiden von Unruhe in der Umgebung der Katze  
14 .Korrekte Verwendung von Spray-Produkten gegen das Kratzen   							    15 .Einrichtung von Spiel- und Schlafplätzen für die Katz    
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
    text.lines()
        .filter { it.trim().length >= 10 }
        .filter { it.length <= 100 }
        .filter { it.trim()[0].isLetter() }
        .filter { it.trim()[0].isUpperCase() }
        .forEach { println(it) }
}

// фильтровать:
//начинается не с большой буквы
//trimm и меньше 5 символов
//более 100 символов
//
// убрать
//убрать все непечатные символы