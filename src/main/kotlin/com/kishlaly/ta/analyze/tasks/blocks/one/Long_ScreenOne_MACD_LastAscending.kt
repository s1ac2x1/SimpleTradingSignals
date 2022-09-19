package com.kishlaly.ta.analyze.tasks.blocks.one

import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.indicators.Indicator
import com.kishlaly.ta.model.indicators.MACD
import com.kishlaly.ta.utils.CollectionUtils
import com.kishlaly.ta.utils.Log

/**
 * the last histogram grows
 */
class Long_ScreenOne_MACD_LastAscending : ScreenOneBlock {
    override fun check(screen: SymbolData): BlockResult {
        val screen_1_MACD = screen.indicator(Indicator.MACD) as List<MACD>
        val screen_1_lastMACD = screen_1_MACD.last()
        val screen_1_preLastMACD = CollectionUtils.getFromEnd(screen_1_MACD, 2)

        val check = screen_1_lastMACD.histogram > screen_1_preLastMACD.histogram
        if (!check) {
            Log.recordCode(BlockResultCode.HISTOGRAM_NOT_ASCENDING_SCREEN_1, screen)
            Log.addDebugLine("The histogram does not grow on the long-term screen")
            return BlockResult(screen.lastQuote, BlockResultCode.HISTOGRAM_NOT_ASCENDING_SCREEN_1)
        }
        return BlockResult(screen.lastQuote, BlockResultCode.OK)
    }
}