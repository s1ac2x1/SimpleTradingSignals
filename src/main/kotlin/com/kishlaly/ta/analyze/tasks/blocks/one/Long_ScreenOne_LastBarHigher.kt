package com.kishlaly.ta.analyze.tasks.blocks.one

import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.utils.Log

/**
 * the last quote is higher than the penultimate one
 */
class Long_ScreenOne_LastBarHigher : ScreenOneBlock {
    override fun check(screen: SymbolData): BlockResult {
        val lastBarHigher = (screen.lastQuote.low > screen.preLastQuote.low
                && screen.lastQuote.high > screen.preLastQuote.high)

        if (!lastBarHigher) {
            Log.recordCode(BlockResultCode.LAST_QUOTES_NOT_ASCENDING_SCREEN_1, screen)
            Log.addDebugLine("The last bar is not higher than the penultimate on the long-term screen")
            return BlockResult(screen.lastQuote, BlockResultCode.LAST_QUOTES_NOT_ASCENDING_SCREEN_1)
        }
        return BlockResult(screen.lastQuote, BlockResultCode.OK)
    }
}