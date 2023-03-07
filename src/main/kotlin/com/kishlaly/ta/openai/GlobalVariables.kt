package com.kishlaly.ta.openai

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.concurrent.atomic.AtomicInteger

val textTokensUsed = AtomicInteger(0)
val pricePerThousandTextTokens = 0.02

val imagesGenerated = AtomicInteger(0)
val perImage = 0.018

val mainOutputFolder = "openai/flow/output"

fun printCosts() {
    val centsForTexts = BigDecimal(pricePerThousandTextTokens * textTokensUsed.get() / 1000)
    val centsForImages = BigDecimal(imagesGenerated.get() * perImage)
    val usd =
        (centsForTexts + centsForImages).setScale(4, RoundingMode.HALF_EVEN).toPlainString()
    println("Costs so far: $$usd [text tokens: ${textTokensUsed.get()}, images: ${imagesGenerated}]")
}