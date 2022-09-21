package com.kishlaly.ta.analyze.functions

import com.kishlaly.ta.config.Context
import com.kishlaly.ta.model.AbstractModel
import com.kishlaly.ta.model.Quote
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.indicators.EMA
import com.kishlaly.ta.model.indicators.Indicator
import com.kishlaly.ta.model.indicators.MACD
import java.util.function.BiFunction
import java.util.function.Function

class TrendFunctions {

    companion object {

        // Main rules of verification:
        // 1. At least half of the last N bars must be the right color (green on an uptrend or red on a downtrend)
        //    Also, all N should not open and close above the EMA26 (the shadow may cross) TODO why so strict?
        // 2. The EMA should rise sequentially
        // 3. if the MACD histogram does not increase consistently - that's ok
        //    the main thing is that it does not descend sequentially when the EMA grows
        //
        // In the future, one might think about such cases:
        // 1) Long-term timeframe, EMA rises, histogram descends smoothly: https://drive.google.com/file/d/1l6aAV-qDseGkBqCQ4-cb3lB-hUy_R95G/view?usp=sharing
        // 2) medium-term timeframe, the oscillator gives a signal, the histogram does not contradict, the price bars too: https://drive.google.com/file/d/1tw_wUR9MXbT2zooD6dmH97bC9AwQCA3U/view?usp=sharing
        // (this case is probably already covered by the first point: check the color of half of the last N bars)
        fun uptrendCheckOnMultipleBars(
            symbolData: SymbolData,
            minBarCount: Int,
            barsToCheck: Int
        ): Boolean {
            return abstractTrendCheckOnMultipleBars(
                symbolData,
                minBarCount,
                barsToCheck,
                { quote: Quote -> quote.open < quote.close },
                { quote: Quote, ema: EMA -> quote.open > ema.value && quote.close > ema.value },
                { next: Double, curr: Double -> next <= curr },
                { curr: Double, next: Double -> curr < next },
                { curr: Double, next: Double -> curr > next }
            )
        }

        fun downtrendCheckOnMultipleBars(symbolData: SymbolData, minBarsCount: Int, barsToCheck: Int): Boolean {
            return abstractTrendCheckOnMultipleBars(
                symbolData,
                minBarsCount,
                barsToCheck,
                { quote: Quote -> quote.open > quote.close },
                { quote: Quote, ema: EMA -> quote.open < ema.value && quote.close < ema.value },
                { next: Double, curr: Double -> next >= curr },
                { curr: Double, next: Double -> curr > next },
                { curr: Double, next: Double -> curr < next }
            )
        }

        private fun abstractTrendCheckOnMultipleBars(
            symbolData: SymbolData,
            minBarsCount: Int,
            barsToCheck: Int,
            barCorrectColor: Function<Quote, Boolean>,
            quoteEmaIntersectionCheck: BiFunction<Quote, EMA, Boolean>,
            emaMoveCheck: BiFunction<Double, Double, Boolean>,
            histogramCheck1: BiFunction<Double, Double, Boolean>,
            histogramCheck2: BiFunction<Double, Double, Boolean>
        ): Boolean {
            val quotes = trim(symbolData.quotes, minBarsCount)
            val ema = trim(symbolData.indicator(Indicator.EMA26)!!, minBarsCount)
            val macd = trim(symbolData.indicator(Indicator.MACD)!!, minBarsCount)
            for (i in quotes.size - barsToCheck until quotes.size) {
                if (!quoteEmaIntersectionCheck.apply(quotes[i] as Quote, ema[i] as EMA)) {
                    return false
                }
            }
            var barsWithCorrectColors = 0
            for (i in quotes.size - barsToCheck until quotes.size) {
                if (barCorrectColor.apply(quotes[i] as Quote)) {
                    barsWithCorrectColors++
                }
            }
            if (barsWithCorrectColors < barsToCheck / 2) {
                return false
            }

            for (i in ema.size - barsToCheck until ema.size - 1) {
                val curr = (ema[i] as EMA).value
                val next = (ema[i + 1] as EMA).value
                if (emaMoveCheck.apply(next, curr)) {
                    return false
                }
            }

            if (Context.trendCheckIncludeHistogram) {
                var histogramMovement1 = 0
                var histogramMovement2 = 0
                var macdMovingConstantly = false
                for (i in macd.size - barsToCheck until macd.size - 1) {
                    val curr = (macd[i] as MACD).histogram
                    val next = (macd[i + 1] as MACD).histogram
                    if (histogramCheck1.apply(curr, next)) {
                        histogramMovement1++
                    }
                    if (histogramCheck2.apply(curr, next)) {
                        histogramMovement2++
                    }
                }
                macdMovingConstantly = histogramMovement1 == barsToCheck - 1
                if (!macdMovingConstantly) {
                    if (histogramMovement2 == barsToCheck - 1) {
                        return false
                    }
                }
            }
            return true
        }

        private fun trim(source: List<AbstractModel>, minSize: Int) = source.subList(source.size - minSize, source.size)

    }

}