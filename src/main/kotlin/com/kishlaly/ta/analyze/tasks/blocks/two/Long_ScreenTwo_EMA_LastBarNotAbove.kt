package com.kishlaly.ta.analyze.tasks.blocks.two

import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.above
import com.kishlaly.ta.model.indicators.EMA
import com.kishlaly.ta.model.indicators.Indicator
import com.kishlaly.ta.utils.Log

/**
 * last bar no higher than EMA13
 */
class Long_ScreenTwo_EMA_LastBarNotAbove : ScreenTwoBlock {
    override fun check(screen: SymbolData): BlockResult {
        val screen_2_EMA13 = screen.indicator(Indicator.EMA13) as List<EMA>
        if (screen.lastQuote above screen_2_EMA13.last()) {
            Log.recordCode(BlockResultCode.LAST_QUOTE_ABOVE_EMA_SCREEN_2, screen)
            Log.addDebugLine("Last bar above the EMA on the second screen")
            return BlockResult(screen.lastQuote, BlockResultCode.LAST_QUOTE_ABOVE_EMA_SCREEN_2)
        }
        return BlockResult(screen.lastQuote, BlockResultCode.OK)
    }
}