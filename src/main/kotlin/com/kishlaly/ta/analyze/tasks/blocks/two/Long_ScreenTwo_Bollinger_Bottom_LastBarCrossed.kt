package com.kishlaly.ta.analyze.tasks.blocks.two

import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.indicators.Bollinger
import com.kishlaly.ta.model.indicators.Indicator
import com.kishlaly.ta.model.isCrossesBollingerBottom
import com.kishlaly.ta.utils.Log

/**
 * the last bar crossed the lower Bollinger band
 */
class Long_ScreenTwo_Bollinger_Bottom_LastBarCrossed : ScreenTwoBlock {
    override fun check(screen: SymbolData): BlockResult {
        val screen_2_Bollinger = screen.indicator(Indicator.BOLLINGER) as List<Bollinger>
        if (!screen.lastQuote.isCrossesBollingerBottom(screen_2_Bollinger.last())) {
            Log.recordCode(BlockResultCode.LAST_QUOTE_NOT_CROSSED_BOLLINGER_BOTTOM_SCREEN_2, screen)
            Log.addDebugLine("The last bar didn't cross the lower Bollinger band")
            return BlockResult(
                screen.lastQuote,
                BlockResultCode.LAST_QUOTE_NOT_CROSSED_BOLLINGER_BOTTOM_SCREEN_2
            )
        }
        return BlockResult(screen.lastQuote, BlockResultCode.OK)
    }
}