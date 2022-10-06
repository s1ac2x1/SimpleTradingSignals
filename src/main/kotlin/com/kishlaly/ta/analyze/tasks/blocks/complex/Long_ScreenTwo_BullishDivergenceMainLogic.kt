package com.kishlaly.ta.analyze.tasks.blocks.complex

import com.kishlaly.ta.analyze.tasks.Divergencies
import com.kishlaly.ta.analyze.tasks.blocks.two.ScreenTwoBlock
import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.HistogramQuote
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.indicators.Indicator
import com.kishlaly.ta.model.indicators.MACD
import com.kishlaly.ta.utils.Dates
import com.kishlaly.ta.utils.Log

class Long_ScreenTwo_BullishDivergenceMainLogic : ScreenTwoBlock {
    override fun check(screen: SymbolData): BlockResult {
        val screenTwoMinBarCount = screen.quotesCount
        val screenTwoMacdValues = screen.indicator(Indicator.MACD) as List<MACD>

        // build an array of quotes with their histograms

        val histogramQuotes = mutableListOf<HistogramQuote>()
        for (i in 0 until screenTwoMinBarCount) {
            histogramQuotes.add(HistogramQuote(screenTwoMacdValues[i].histogram, screen.quote(i)))
        }

        // the minimum bar of the histogram for the whole period and the corresponding quotation

        val quoteWithLowestHistogram = histogramQuotes.minByOrNull { it.histogramValue }

        // the index of this column in the histogramQuotes array

        var indexOfMinHistogram = -1
        for (i in histogramQuotes.indices) {
            if (histogramQuotes[i].histogramValue == quoteWithLowestHistogram!!.histogramValue) {
                indexOfMinHistogram = i
                break
            }
        }

        // not critical, but it is better that the price at the level of the minimum histogram was also the minimum from the beginning of the period to this point
        var minimalPriceForRangeUpToLowestHistogram: Double

        // if the first bar contains a minimum histogram
        if (indexOfMinHistogram == 0) {
            minimalPriceForRangeUpToLowestHistogram = histogramQuotes[0].quote.close
        } else {
            //minimalPriceForRangeUpToLowestHistogram = histogramQuotes.subList(0, indexOfMinHistogram).minBy { it.quote.close }
            minimalPriceForRangeUpToLowestHistogram =
                histogramQuotes.subList(0, indexOfMinHistogram).minWith(compareBy { it.quote.close }).quote.close
        }

        val histogramQuoteWithMinimanPriceForTheWholeRange =
            histogramQuotes.filter { it.quote.close == minimalPriceForRangeUpToLowestHistogram }.first()
        if (quoteWithLowestHistogram!!.quote.close > minimalPriceForRangeUpToLowestHistogram
        ) {
            Log.addDebugLine(
                "Внимание: цена на дне гистограммы А (" + quoteWithLowestHistogram!!.histogramValue + " " + Dates.beautifyQuoteDate(
                    quoteWithLowestHistogram.quote
                ) + ") не самая низкая в диапазоне"
            )
        }

        // Finding a high that follows a past low ("breaking a bearish backbone")
        // The histogram should pop out above zero


        var indexOfMaxHistogramBarAfterLowestLow = 0

        val histogramQuotesAfterLowestLow = histogramQuotes.subList(indexOfMinHistogram, histogramQuotes.size)
        val quoteWithHighestHistogramAfterLowestLow =
            histogramQuotesAfterLowestLow.maxWith(compareBy { it.histogramValue })

        if (quoteWithHighestHistogramAfterLowestLow.histogramValue <= 0) {
            Log.recordCode(BlockResultCode.BEARISH_BACKBONE_NOT_CRACKED_SCREEN_2, screen)
            Log.addDebugLine("there was no fracture of the bear's backbone")
            return BlockResult(screen.lastQuote, BlockResultCode.BEARISH_BACKBONE_NOT_CRACKED_SCREEN_2)
        }

        // what is the maximum index

        for (i in indexOfMinHistogram until histogramQuotes.size) {
            if (histogramQuotes[i].histogramValue == quoteWithHighestHistogramAfterLowestLow.histogramValue) {
                indexOfMaxHistogramBarAfterLowestLow = i
                break
            }
        }

        // now we need to find the histogram zero line crossings from top to bottom

        val histogramQuotesFromMaxBar =
            histogramQuotes.subList(indexOfMaxHistogramBarAfterLowestLow, histogramQuotes.size)
        var crossedZero = false
        var quoteWhenHistogramCrossedZeroFromTop: HistogramQuote? = null
        for (i in indexOfMaxHistogramBarAfterLowestLow until histogramQuotes.size) {
            if (histogramQuotes[i].histogramValue < 0) {
                crossedZero = true
                quoteWhenHistogramCrossedZeroFromTop = histogramQuotes[i]
                break
            }
        }

        // check the price values at the histogram minimum and maximum

        if (crossedZero) {
            val priceInLowestHistogramBar = quoteWithLowestHistogram!!.quote.close
            val priceWhenHitogramCrossedZeroFromTop = quoteWhenHistogramCrossedZeroFromTop!!.quote.close

            // between the point of intersection of the histogram zero from vertex B and the end of the chart there should be no positive bars of the histogram
            // because it may already be an old divergence
            // howerver, it doesn't filter cases like https://drive.google.com/file/d/1FYm6rib--9VmlNucXCmzdJnUIzrYjzPc/view?usp=sharing

            var indexOfQuoteWhenHistogramCrossedZeroFromTop = 0
            for (i in histogramQuotes.indices) {
                if (histogramQuotes[i].quote == quoteWhenHistogramCrossedZeroFromTop.quote) {
                    indexOfQuoteWhenHistogramCrossedZeroFromTop = i
                    break
                }
            }

            // Trying to get rid of the situation https://drive.google.com/file/d/1OT1LBAdH1cZiGYJw0PDrwQ3NTpslqygY/view?usp=sharing

            var foundFirstPositive = false
            var foundSecondPositive = false
            var foundSecondNegativeAfterLowestLow = false
            var indexOfSecondPositive = -1
            for (i in indexOfMinHistogram until histogramQuotes.size) {
                val histogramQuote = histogramQuotes[i]
                val histogramValue = histogramQuote.histogramValue
                if (histogramValue > 0) {
                    if (!foundSecondPositive) {
                        foundFirstPositive = true
                    }
                    if (foundSecondNegativeAfterLowestLow) {
                        foundSecondPositive = true
                        indexOfSecondPositive = i
                        break
                    }
                }
                if (histogramValue < 0) {
                    if (foundFirstPositive) {
                        foundSecondNegativeAfterLowestLow = true
                    }
                }
            }
            if (foundSecondPositive) {
                Log.recordCode(BlockResultCode.HISTOGRAM_MULTIPLE_POSITIVE_ISLANDS, screen)
                Log.addDebugLine("In the point " + Dates.beautifyQuoteDate(histogramQuotes[indexOfSecondPositive].quote) + " a second positive area was found")
                return BlockResult(screen.lastQuote, BlockResultCode.HISTOGRAM_MULTIPLE_POSITIVE_ISLANDS)
            }

            // this could be the beginning of a triple divergence if the price keeps falling
            // example: https://drive.google.com/file/d/1hm-LUXXtpghwNMnfbz93diLQOIyjy9dV/view?usp=sharing

            var histogramWentAboveZeroAgainAfterHistogramCrossedZeroFromTop = false
            for (i in indexOfQuoteWhenHistogramCrossedZeroFromTop until histogramQuotes.size) {
                if (histogramQuotes[i].histogramValue > 0) {
                    histogramWentAboveZeroAgainAfterHistogramCrossedZeroFromTop = true
                }
            }

            if (histogramWentAboveZeroAgainAfterHistogramCrossedZeroFromTop) {
                if (Divergencies.BullishConfig.ALLOW_MULTIPLE_ISLANDS) {
                    if (histogramQuotes[histogramQuotes.size - 1].quote.close
                        < priceInLowestHistogramBar
                    ) {
                        // OK, we observe manually
                        Log.addDebugLine(
                            "Note: after crossing the histogram zero from vertex B (" + Dates.beautifyQuoteDate(
                                quoteWhenHistogramCrossedZeroFromTop.quote
                            ) + ") another area of positive histograms was encountered"
                        )
                    } else {
                        Log.recordCode(BlockResultCode.HISTOGRAM_ISLANDS_HIGHER_PRICE, screen)
                        Log.addDebugLine("After the bottom of the histogram A there are several positive areas of the histograms, but at the edge the price is higher than in A")
                        return BlockResult(screen.lastQuote, BlockResultCode.HISTOGRAM_ISLANDS_HIGHER_PRICE)
                    }
                } else {
                    Log.addDebugLine(
                        "Forbidden: after the histogram crosses zero from vertex B (" + Dates.beautifyQuoteDate(
                            quoteWhenHistogramCrossedZeroFromTop.quote
                        ) + ") another area of positive histograms was encountered"
                    )
                }
            }

            // the second bottom of the histogram should not be lower than half of the first bottom

            val lowestHistogramAfterCrossedZeroFromTop = histogramQuotes
                .subList(indexOfQuoteWhenHistogramCrossedZeroFromTop, histogramQuotes.size)
                .minWith(compareBy { it.histogramValue }).histogramValue
            if (Math.abs(lowestHistogramAfterCrossedZeroFromTop) >= Math.abs(quoteWithLowestHistogram.histogramValue) / 100 * 60) {
                Log.recordCode(BlockResultCode.HISTOGRAM_SECOND_BOTTOM_RATIO, screen)
                Log.addDebugLine("The second bottom of the histogram is larger than " + Divergencies.BullishConfig.SECOND_BOTTOM_RATIO + "% of the first bottom depth")
                return BlockResult(screen.lastQuote, BlockResultCode.HISTOGRAM_SECOND_BOTTOM_RATIO)
            }

            // the last bar of the histogram should be smaller than the previous one
            val preLast = histogramQuotes[histogramQuotes.size - 2]
            val last = histogramQuotes[histogramQuotes.size - 1]
            if (Math.abs(last.histogramValue) >= Math.abs(preLast.histogramValue)) {
                Log.recordCode(BlockResultCode.HISTOGRAM_LAST_BAR_NOT_LOWER, screen)
                Log.addDebugLine("The last bar of the histogram is not lower than the previous one")
                return BlockResult(screen.lastQuote, BlockResultCode.HISTOGRAM_LAST_BAR_NOT_LOWER)
            }

            // exclude long tails of negative histograms at the right edge, which often occur in a downtrend on a larger timeframe

            val tailCount =
                histogramQuotes.subList(indexOfQuoteWhenHistogramCrossedZeroFromTop, histogramQuotes.size).stream()
                    .count()
            if (tailCount >= Divergencies.BullishConfig.MAX_TAIL_SIZE) {
                Log.recordCode(BlockResultCode.NEGATIVE_HISTOGRAMS_LIMIT, screen)
                Log.addDebugLine("At the right edge has piled " + tailCount + " negative histograms (limit: " + Divergencies.BullishConfig.MAX_TAIL_SIZE + ")")
                return BlockResult(screen.lastQuote, BlockResultCode.NEGATIVE_HISTOGRAMS_LIMIT)
            }

            // the price should form a new valley
            if (priceWhenHitogramCrossedZeroFromTop
                < priceInLowestHistogramBar
            ) {
            } else {
                Log.recordCode(BlockResultCode.DIVERGENCE_FAIL_AT_ZERO, screen)
                Log.addDebugLine("No divergence: on the slope to zero from B the price is higher than in A")
                return BlockResult(screen.lastQuote, BlockResultCode.DIVERGENCE_FAIL_AT_ZERO)
            }

        } else {
            Log.recordCode(BlockResultCode.DIVERGENCE_FAIL_AT_TOP, screen)
            Log.addDebugLine("the histogram did not descend from the top of B")
            return BlockResult(screen.lastQuote, BlockResultCode.DIVERGENCE_FAIL_AT_TOP)
        }

        return BlockResult(screen.lastQuote, BlockResultCode.OK)
    }
}