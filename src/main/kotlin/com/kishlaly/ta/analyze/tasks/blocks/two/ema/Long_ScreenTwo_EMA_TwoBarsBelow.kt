package com.kishlaly.ta.analyze.tasks.blocks.two.ema

import com.kishlaly.ta.analyze.tasks.blocks.two.ScreenTwoBlock
import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.below
import com.kishlaly.ta.model.indicators.EMA
import com.kishlaly.ta.model.indicators.Indicator
import com.kishlaly.ta.utils.CollectionUtils
import com.kishlaly.ta.utils.Log

/**
 * the last two bars are completely below EMA13
 */
class Long_ScreenTwo_EMA_TwoBarsBelow : ScreenTwoBlock {
    override fun check(screen: SymbolData): BlockResult {
        val screen_2_EMA13 = screen.indicator(Indicator.EMA13) as List<EMA>
        val ema2 = CollectionUtils.getFromEnd<EMA>(screen_2_EMA13, 2)
        val ema1 = screen_2_EMA13.last()

        val lastQuotesBelowEMA = screen.preLastQuote below ema2 && screen.lastQuote below ema1
        if (!lastQuotesBelowEMA) {
            Log.recordCode(BlockResultCode.QUOTES_NOT_BELOW_EMA_SCREEN_2, screen)
            Log.addDebugLine("The last two quotes are not below EMA13")
            return BlockResult(screen.lastQuote, BlockResultCode.QUOTES_NOT_BELOW_EMA_SCREEN_2)
        }
        return BlockResult(screen.lastQuote, BlockResultCode.OK)

    }
}