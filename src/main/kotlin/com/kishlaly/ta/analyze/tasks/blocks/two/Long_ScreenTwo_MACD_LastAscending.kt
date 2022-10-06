package com.kishlaly.ta.analyze.tasks.blocks.two

import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.indicators.Indicator
import com.kishlaly.ta.model.indicators.MACD
import com.kishlaly.ta.utils.Log

/**
 * the last histogram grows
 */
class Long_ScreenTwo_MACD_LastAscending : ScreenTwoBlock {
    override fun check(screen: SymbolData): BlockResult {
        val screen_2_MACD = screen.indicator(Indicator.MACD) as List<MACD>
        val screen_2_lastMACD = screen_2_MACD.last()
        val screen_2_preLastMACD = screen_2_MACD[screen_2_MACD.size - 2]

        val ascending = screen_2_lastMACD.histogram > screen_2_preLastMACD.histogram
        if (!ascending) {
            Log.recordCode(BlockResultCode.HISTOGRAM_NOT_ASCENDING_SCREEN_2, screen)
            Log.addDebugLine("The histogram does not grow on the second screen")
            return BlockResult(screen.lastQuote, BlockResultCode.HISTOGRAM_NOT_ASCENDING_SCREEN_2)
        }

        return BlockResult(screen.lastQuote, BlockResultCode.OK)
    }
}