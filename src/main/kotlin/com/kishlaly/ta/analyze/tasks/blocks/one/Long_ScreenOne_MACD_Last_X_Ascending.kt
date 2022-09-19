package com.kishlaly.ta.analyze.tasks.blocks.one

import com.kishlaly.ta.analyze.tasks.ThreeDisplays
import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.indicators.Indicator
import com.kishlaly.ta.model.indicators.MACD
import com.kishlaly.ta.utils.Log

/**
 * the last X histograms grow consecutively
 */
class Long_ScreenOne_MACD_Last_X_Ascending : ScreenOneBlock {
    override fun check(screen: SymbolData): BlockResult {
        if (ThreeDisplays.Config.EMA26_TOTAL_BARS_CHECK < 0) {
            println("ThreeDisplays.Config.EMA26_TOTAL_BARS_CHECK not set")
            return BlockResult.configurationError(screen.lastQuote)
        }
        val screen_1_MACD = screen.indicator(Indicator.MACD) as List<MACD>
        var count = 0
        for (i in screen_1_MACD.size - ThreeDisplays.Config.EMA26_TOTAL_BARS_CHECK until screen_1_MACD.size - 1) {
            val currentValue = screen_1_MACD[i].histogram
            val nextValue = screen_1_MACD[i + 1].histogram
            if (currentValue < nextValue) {
                count++
            }
        }
        if (count < ThreeDisplays.Config.EMA26_TOTAL_BARS_CHECK) {
            Log.recordCode(BlockResultCode.X_HISTOGRAMS_NOT_ASCENDING_SCREEN_1, screen)
            Log.addDebugLine(ThreeDisplays.Config.EMA26_TOTAL_BARS_CHECK.toString() + " histograms do not grow on the long-term screen")
            return BlockResult(screen.lastQuote, BlockResultCode.X_HISTOGRAMS_NOT_ASCENDING_SCREEN_1)
        }
        return BlockResult(screen.lastQuote, BlockResultCode.OK)
    }
}