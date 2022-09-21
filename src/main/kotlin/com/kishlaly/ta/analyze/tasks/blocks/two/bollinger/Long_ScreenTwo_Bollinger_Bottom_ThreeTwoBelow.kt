package com.kishlaly.ta.analyze.tasks.blocks.two.bollinger

import com.kishlaly.ta.analyze.tasks.blocks.two.ScreenTwoBlock
import com.kishlaly.ta.model.*
import com.kishlaly.ta.model.indicators.Bollinger
import com.kishlaly.ta.model.indicators.Indicator
import com.kishlaly.ta.utils.CollectionUtils
import com.kishlaly.ta.utils.Log

/**
 * the third and second quotes from the end below the bottom Bollinger band
 */
class Long_ScreenTwo_Bollinger_Bottom_ThreeTwoBelow : ScreenTwoBlock {
    override fun check(screen: SymbolData): BlockResult {
        val screen_2_Bollinger = screen.indicator(Indicator.BOLLINGER) as List<Bollinger>
        val quote_3 = CollectionUtils.getFromEnd<Quote>(screen.quotes, 3)
        val quote_2 = CollectionUtils.getFromEnd<Quote>(screen.quotes, 2)
        val bollinger_3 = CollectionUtils.getFromEnd<Bollinger>(screen_2_Bollinger, 3)
        val bollinger_2 = CollectionUtils.getFromEnd<Bollinger>(screen_2_Bollinger, 2)

        val below = quote_3 belowBollingerBottom bollinger_3 && quote_2 belowBollingerBottom bollinger_2
        if (!below) {
            Log.recordCode(BlockResultCode.QUOTE_3_AND_QUOTE_2_NOT_BELOW_BOLLINGER_BOTTOM_SCREEN_2, screen)
            Log.addDebugLine("The third and second from the end quotes are not lower than the bottom Bollinger band on the second screen")
            return BlockResult(
                screen.lastQuote,
                BlockResultCode.QUOTE_3_AND_QUOTE_2_NOT_BELOW_BOLLINGER_BOTTOM_SCREEN_2
            )
        }
        return BlockResult(screen.lastQuote, BlockResultCode.OK)
    }
}