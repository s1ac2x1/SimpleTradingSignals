package com.kishlaly.ta.utils

import com.kishlaly.ta.analyze.TaskType
import com.kishlaly.ta.cache.CacheReader
import com.kishlaly.ta.model.indicators.EMA
import com.kishlaly.ta.model.indicators.Indicator
import com.kishlaly.ta.model.indicators.MACD

fun main() {
    val symbol = "LUMN"
    val screen1 = CacheReader.getSymbolData(TaskType.THREE_DISPLAYS_BUY.getTimeframeIndicators(1), symbol)
    val screen2 = CacheReader.getSymbolData(TaskType.THREE_DISPLAYS_BUY.getTimeframeIndicators(2), symbol)

    val screen_2_EMA13: List<EMA> = screen2.indicators.get(Indicator.EMA13) as List<EMA>
    val screen_2_MACD: List<MACD> = screen2.indicators.get(Indicator.MACD) as List<MACD>

    val macdVariants = mutableListOf<MACD>()
    val macdPairs = mutableListOf<Pair<MACD, MACD>>()

    for (i in 0 until screen_2_EMA13.size - 5) {
        if (screen_2_EMA13[i].value < screen_2_EMA13[i + 1].value
            && screen_2_EMA13[i + 1].value < screen_2_EMA13[i + 2].value
            && screen_2_EMA13[i + 2].value < screen_2_EMA13[i + 3].value
            && screen_2_EMA13[i + 3].value < screen_2_EMA13[i + 4].value
            && screen_2_EMA13[i + 4].value < screen_2_EMA13[i + 5].value
        ) {
            macdVariants.add(screen_2_MACD[i])
            macdPairs.add(Pair(screen_2_MACD[i], screen_2_MACD[i + 1]))
        }
    }

    var macdBelowZeroAndGrows = 0
    var macdAboveZeroAndGrows = 0
    var macdCrossingZeroAndGrows = 0

    for (pair in macdPairs) {
        if (pair.first.histogram < 0 && pair.second.histogram < 0 && pair.second.histogram > pair.first.histogram) {
            macdBelowZeroAndGrows++
        }
        if (pair.first.histogram > 0 && pair.second.histogram > 0 && pair.second.histogram > pair.first.histogram) {
            macdAboveZeroAndGrows++
        }
        if (pair.first.histogram <= 0 && pair.second.histogram >= 0 && pair.second.histogram > pair.first.histogram) {
            macdCrossingZeroAndGrows++
        }
    }

    println("MACD < 0 and grows: ${macdBelowZeroAndGrows}")
    println("MACD > 0 and grows: ${macdAboveZeroAndGrows}")
    println("MACD crossing 0 and grows: ${macdCrossingZeroAndGrows}")
}