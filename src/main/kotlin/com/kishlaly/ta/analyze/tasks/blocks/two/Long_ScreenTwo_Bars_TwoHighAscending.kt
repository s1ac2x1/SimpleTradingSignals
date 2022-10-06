package com.kishlaly.ta.analyze.tasks.blocks.two

import com.kishlaly.ta.analyze.tasks.blocks.two.ScreenTwoBlock
import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.utils.Log

/**
 * high of the last column above the penultimate one
 */
class Long_ScreenTwo_Bars_TwoHighAscending : ScreenTwoBlock {
    override fun check(screen: SymbolData): BlockResult {
        val check = screen.lastQuote.high > screen.preLastQuote.high
        if (!check) {
            Log.recordCode(BlockResultCode.LAST_QUOTES_NOT_ASCENDING_SCREEN_2, screen)
            Log.addDebugLine("High of the last column is not higher than the penultimate one on the second screen")
            return BlockResult(screen.lastQuote, BlockResultCode.LAST_QUOTES_NOT_ASCENDING_SCREEN_2)
        }
        return BlockResult(screen.lastQuote, BlockResultCode.OK)
    }
}