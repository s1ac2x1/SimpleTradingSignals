package com.kishlaly.ta.analyze.tasks.blocks.two

import com.kishlaly.ta.analyze.tasks.blocks.two.ScreenTwoBlock
import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.isGreen
import com.kishlaly.ta.utils.Log

/**
 * The last two quotes are green
 */
class Long_ScreenTwo_Bars_TwoGreen : ScreenTwoBlock {
    override fun check(screen: SymbolData): BlockResult {
        val bothAreGreen = screen.lastQuote.isGreen() && screen.preLastQuote.isGreen()
        if (!bothAreGreen) {
            Log.recordCode(BlockResultCode.LAST_QUOTES_NOT_GREEN_SCREEN_2, screen)
            Log.addDebugLine("The last two quotes are not green on the second screen")
            return BlockResult(screen.lastQuote, BlockResultCode.LAST_QUOTES_NOT_GREEN_SCREEN_2)
        }
        return BlockResult(screen.lastQuote, BlockResultCode.OK)
    }
}