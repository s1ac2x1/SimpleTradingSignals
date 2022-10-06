package com.kishlaly.ta.analyze.tasks.blocks.two

import com.kishlaly.ta.analyze.tasks.blocks.two.ScreenTwoBlock
import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.utils.Log

/**
 * Last quote is green
 */
class Long_ScreenTwo_Bars_LastGreen : ScreenTwoBlock {
    override fun check(screen: SymbolData): BlockResult {
        val lastQuote = screen.lastQuote

        val green = lastQuote.close > lastQuote.open
        if (!green) {
            Log.recordCode(BlockResultCode.LAST_QUOTE_NOT_GREEN_SCREEN_2, screen)
            Log.addDebugLine("The last quote is not green on the second screen")
            return BlockResult(lastQuote, BlockResultCode.LAST_QUOTE_NOT_GREEN_SCREEN_2)
        }
        return BlockResult(lastQuote, BlockResultCode.OK)
    }
}