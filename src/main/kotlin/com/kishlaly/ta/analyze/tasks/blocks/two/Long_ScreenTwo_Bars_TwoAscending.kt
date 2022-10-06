package com.kishlaly.ta.analyze.tasks.blocks.two

import com.kishlaly.ta.analyze.tasks.blocks.two.ScreenTwoBlock
import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.utils.Log

/**
 * the last two bars go up (quote.low & quote.high)
 */
class Long_ScreenTwo_Bars_TwoAscending : ScreenTwoBlock {
    override fun check(screen: SymbolData): BlockResult {
        val lowAndHightAscending = screen.preLastQuote.low < screen.lastQuote.low
                && screen.preLastQuote.high < screen.lastQuote.high
        if (!lowAndHightAscending) {
            Log.recordCode(BlockResultCode.LAST_QUOTES_NOT_ASCENDING_SCREEN_2, screen)
            Log.addDebugLine("The last two quotes do not grow consistently on the second screen")
            return BlockResult(screen.lastQuote, BlockResultCode.LAST_QUOTES_NOT_ASCENDING_SCREEN_2)
        }
        return BlockResult(screen.lastQuote, BlockResultCode.OK)
    }
}