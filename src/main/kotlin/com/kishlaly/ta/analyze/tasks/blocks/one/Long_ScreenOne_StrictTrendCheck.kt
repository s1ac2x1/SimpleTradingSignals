package com.kishlaly.ta.analyze.tasks.blocks.one

import com.kishlaly.ta.analyze.functions.TrendFunctions
import com.kishlaly.ta.analyze.tasks.ThreeDisplays
import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.Quote
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.utils.Log
import com.kishlaly.ta.utils.Quotes.Companion.resolveMinBarsCount

/**
 * Strict trend check using quotes, EMA26 and MACD
 */
class Long_ScreenOne_StrictTrendCheck : ScreenOneBlock {
    override fun check(screen: SymbolData): BlockResult {
        val uptrendCheckOnMultipleBars = TrendFunctions.uptrendCheckOnMultipleBars(
            screen,
            resolveMinBarsCount(screen.timeframe),
            ThreeDisplays.Config.NUMBER_OF_EMA26_VALUES_TO_CHECK
        )
        if (!uptrendCheckOnMultipleBars) {
            Log.recordCode(BlockResultCode.NO_UPTREND_SCREEN_1, screen)
            Log.addDebugLine("No uptrend detected on the long-term screen")
            return BlockResult(Quote.NaN(), BlockResultCode.NO_UPTREND_SCREEN_1)
        }
        return BlockResult(screen.lastQuote, BlockResultCode.OK)

    }
}