package com.kishlaly.ta.analyze.tasks.blocks.one

import com.kishlaly.ta.analyze.functions.TrendFunctions
import com.kishlaly.ta.analyze.tasks.ThreeDisplays
import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.utils.Log
import com.kishlaly.ta.utils.Quotes

class Short_ScreenOne_StrictTrendCheck : ScreenOneBlock {
    override fun check(screen: SymbolData): BlockResult {
        val downtrendCheckOnMultipleBars = TrendFunctions.downtrendCheckOnMultipleBars(
            screen,
            Quotes.resolveMinBarsCount(screen.timeframe),
            ThreeDisplays.Config.NUMBER_OF_EMA26_VALUES_TO_CHECK
        )
        if (!downtrendCheckOnMultipleBars) {
            Log.recordCode(BlockResultCode.NO_DOWNTREND_SCREEN_1, screen)
            Log.addDebugLine("No downtrend detected on the long-term screen")
            return BlockResult(screen.lastQuote, BlockResultCode.NO_DOWNTREND_SCREEN_1)
        }
        return BlockResult(screen.lastQuote, BlockResultCode.OK)
    }
}