package com.kishlaly.ta.analyze.tasks.blocks.two

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
        if (quoteWithLowestHistogram.quote.close > minimalPriceForRangeUpToLowestHistogram
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

    }
}