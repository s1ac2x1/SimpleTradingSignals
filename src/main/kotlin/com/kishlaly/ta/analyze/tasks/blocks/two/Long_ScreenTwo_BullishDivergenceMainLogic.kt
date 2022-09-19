package com.kishlaly.ta.analyze.tasks.blocks.two

import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.HistogramQuote
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.indicators.Indicator
import com.kishlaly.ta.model.indicators.MACD

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
            minimalPriceForRangeUpToLowestHistogram = histogramQuotes.subList(0, indexOfMinHistogram).
        }

    }
}