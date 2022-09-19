package com.kishlaly.ta.analyze.tasks.blocks.one

import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.utils.Log

/**
 * the last quote is green
 */
class Long_ScreenOne_LastBarGreen : ScreenOneBlock {
    override fun check(screen: SymbolData): BlockResult {
        val lastBarIsGreen = screen.lastQuote.open < screen.lastQuote.close
        if (!lastBarIsGreen) {
            Log.recordCode(BlockResultCode.LAST_QUOTE_NOT_GREEN_SCREEN_1, screen)
            Log.addDebugLine("The last quote is not green on the long-term screen")
            return BlockResult(screen.lastQuote, BlockResultCode.LAST_QUOTE_NOT_GREEN_SCREEN_1)
        }
        return BlockResult(screen.lastQuote, BlockResultCode.OK)

    }
}