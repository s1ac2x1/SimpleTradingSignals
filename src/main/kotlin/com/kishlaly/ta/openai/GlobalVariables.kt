package com.kishlaly.ta.openai

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.concurrent.atomic.AtomicInteger

val textTokensUsed = AtomicInteger(0)
val centsPerThousandTextTokens = 2

val imagesGenerated = AtomicInteger(0)
val centsPerImage = 1.8

fun printCosts() {
    val centsForTexts = BigDecimal(textTokensUsed.get() / 1000 * centsPerThousandTextTokens)
    val centsForImages = BigDecimal(imagesGenerated.get() * centsPerImage)
    val usd =
        (centsForTexts + centsForImages).divide(BigDecimal(100)).setScale(2, RoundingMode.HALF_EVEN).toPlainString()
    println(">>> Costs so far: $$usd")
}