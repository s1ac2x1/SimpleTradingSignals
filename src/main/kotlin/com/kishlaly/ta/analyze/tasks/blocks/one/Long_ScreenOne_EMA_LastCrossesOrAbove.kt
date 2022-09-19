package com.kishlaly.ta.analyze.tasks.blocks.one

import com.kishlaly.ta.model.*
import com.kishlaly.ta.model.indicators.EMA
import com.kishlaly.ta.model.indicators.Indicator
import com.kishlaly.ta.utils.Log

/**
 * the last bar crosses the EMA26 or higher
 */
class Long_ScreenOne_EMA_LastCrossesOrAbove : ScreenOneBlock {
    override fun check(screen: SymbolData): BlockResult {
        val screen_1_EMA26 = screen.indicator(Indicator.EMA26) as List<EMA>
        val lasteEMA26 = screen_1_EMA26.last().value
        val lastBarCrossing = screen.lastQuote.isCrossesEMA(lasteEMA26)
        val lastBarAbove = screen.lastQuote.isAboveEMA(lasteEMA26)

        if (!lastBarCrossing || !lastBarAbove) {
            Log.recordCode(BlockResultCode.LAST_QUOTE_NOT_CROSSING_OR_NOT_ABOVE_EMA_SCREEN_1, screen)
            Log.addDebugLine("The last bar does not cross or is not above the EMA on the long term screen")
            return BlockResult(screen.lastQuote, BlockResultCode.LAST_QUOTE_NOT_CROSSING_OR_NOT_ABOVE_EMA_SCREEN_1)
        }

        return BlockResult(screen.lastQuote, BlockResultCode.OK)
    }
}