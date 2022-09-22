package com.kishlaly.ta.analyze.tasks.blocks.two.ema

import com.kishlaly.ta.analyze.tasks.blocks.two.ScreenTwoBlock
import com.kishlaly.ta.model.*
import com.kishlaly.ta.model.indicators.EMA
import com.kishlaly.ta.utils.Log
import com.kishlaly.ta.utils.SymbolDataUtils

/**
 * to filter the situation when the third and second cross EMA13, and the last one is entirely lower (that is, the moment is already lost)
 */
class Short_ScreenTwo_EMA_LastBarTooLow : ScreenTwoBlock {
    override fun check(screen: SymbolData): BlockResult {
        val ema = SymbolDataUtils(screen, EMA::class.java)

        if (screen.lastQuote(3) crosses ema[3]
            && screen.lastQuote(2) crosses ema[2]
            && screen.lastQuote(1) below ema[1]
        ) {
            Log.recordCode(BlockResultCode.LAST_BAR_BELOW_SCREEN_2, screen)
            Log.addDebugLine("The third and second crossed the EMA13, and the last is completely below")
            return BlockResult(screen.lastQuote, BlockResultCode.LAST_BAR_BELOW_SCREEN_2)
        }

        return BlockResult(screen.lastQuote, BlockResultCode.OK)
    }
}