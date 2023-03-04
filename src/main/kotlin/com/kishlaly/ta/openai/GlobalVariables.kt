package com.kishlaly.ta.openai

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.concurrent.atomic.AtomicInteger

val textTokensUsed = AtomicInteger(0)
val pricePerThousandTextTokens = 0.02

val imagesGenerated = AtomicInteger(0)
val pricePerImage = 0.018

fun getCost(): Double {
    val text =
        BigDecimal(textTokensUsed.get() / 1000 * pricePerThousandTextTokens)
            .setScale(2, RoundingMode.HALF_EVEN)
            .toDouble()
    val images = BigDecimal(imagesGenerated.get() * pricePerImage)
        .setScale(2, RoundingMode.HALF_EVEN)
        .toDouble()
    return text + images
}