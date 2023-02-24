package com.kishlaly.ta

import com.kishlaly.ta.analyze.TaskType
import com.kishlaly.ta.cache.CacheReader
import com.kishlaly.ta.model.indicators.ElderForceIndex
import com.kishlaly.ta.model.indicators.Indicator
import java.io.File

fun main() {
//    val screen2 = CacheReader.getSymbolData(TaskType.THREE_DISPLAYS_BUY.getTimeframeIndicators(2), "LUMN")
//    println(screen2.lastQuote.nativeDate)
//    val efi: List<ElderForceIndex> = screen2.indicators.get(Indicator.EFI) as List<ElderForceIndex>
//    println(efi)
    val s = "asdf&34?()3422"
    val re = Regex("\n\n\n")
    val s2 = re.replace(s, "_")
    // Was_sind_die_10_beliebtesten_Katzenrassen?.txt
    val content = File("openai/output/Was_sind_die_10_beliebtesten_Katzenrassen?.txt").readText()
    val contentFixed = re.replace(content, "\n\n")
    println(contentFixed)
}