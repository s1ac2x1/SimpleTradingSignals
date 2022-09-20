package com.kishlaly.ta.analyze.tasks.blocks.two.ema

import com.kishlaly.ta.analyze.tasks.blocks.two.ScreenTwoBlock
import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.Quote
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.indicators.EMA
import com.kishlaly.ta.model.indicators.Indicator
import com.kishlaly.ta.utils.CollectionUtils
import com.kishlaly.ta.utils.Log

/**
 * it is necessary to filter the situation when the third and second cross EMA13, and the last one is entirely higher (the moment of entering the trade is missed)
 * the third can open and close higher, and this is acceptable: https://drive.google.com/file/d/15XkXFKBQbTjeNjBn03NrF9JawCBFaO5t/view?usp=sharing
 */
class Long_ScreenTwo_EMA_LastBarTooHigh : ScreenTwoBlock {
    override fun check(screen: SymbolData): BlockResult {
        val quote3 = CollectionUtils.getFromEnd<Quote>(screen.allQuotes, 3)
        val quote2 = CollectionUtils.getFromEnd<Quote>(screen.allQuotes, 2)
        val quote1 = screen.lastQuote

        val screen_2_EMA13 = screen.indicator(Indicator.EMA13) as List<EMA>
        val ema3 = CollectionUtils.getFromEnd<EMA>(screen_2_EMA13, 3)
        val ema2 = CollectionUtils.getFromEnd<EMA>(screen_2_EMA13, 2)
        val ema1 = screen_2_EMA13.last()

        val thirdCrossesEMA13 = quote3.low < ema3.value && quote3.high > ema3.value
        val secondCrossesEMA13 = quote2.low < ema2.value && quote2.high > ema2.value
        val lastAboveEMA13 = quote1.low > ema1.value && quote1.high > ema1.value

        if (thirdCrossesEMA13 && secondCrossesEMA13 && lastAboveEMA13) {
            Log.recordCode(BlockResultCode.LAST_BAR_ABOVE_SCREEN_2, screen)
            Log.addDebugLine("The third and second crossed the EMA13, and the last is completely above")
            return BlockResult(screen.lastQuote, BlockResultCode.LAST_BAR_ABOVE_SCREEN_2)
        }
        return BlockResult(screen.lastQuote, BlockResultCode.OK)

    }
}