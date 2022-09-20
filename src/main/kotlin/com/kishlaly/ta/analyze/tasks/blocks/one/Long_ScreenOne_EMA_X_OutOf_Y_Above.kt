package com.kishlaly.ta.analyze.tasks.blocks.one

import com.kishlaly.ta.analyze.tasks.ThreeDisplays
import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.above
import com.kishlaly.ta.model.indicators.EMA
import com.kishlaly.ta.model.indicators.Indicator
import com.kishlaly.ta.utils.Log

/**
 * of the latest {TOTAL} quotes at least {ABOVE} is completely above EMA26
 */
class Long_ScreenOne_EMA_X_OutOf_Y_Above : ScreenOneBlock {

    override fun check(screen: SymbolData): BlockResult {
        if (ThreeDisplays.Config.EMA26_TOTAL_BARS_CHECK < 0 || ThreeDisplays.Config.EMA26_ABOVE_BARS < 0) {
            println("ThreeDisplays.Config is wrong")
            return BlockResult.configurationError(screen.lastQuote)
        }

        val screen_1_EMA26 = screen.indicator(Indicator.EMA26) as List<EMA>
        var aboveEMA26 = 0
        for (i in screen_1_EMA26.size - ThreeDisplays.Config.EMA26_TOTAL_BARS_CHECK until screen_1_EMA26.size) {
            if (screen.quote(i) above screen_1_EMA26[i]) {
                aboveEMA26++
            }
        }
        if (aboveEMA26 < ThreeDisplays.Config.EMA26_ABOVE_BARS) {
            Log.recordCode(BlockResultCode.NOT_ALL_NEEDED_QUOTES_ABOUT_EMA_SCREEN_1, screen)
            Log.addDebugLine("Out of the last " + ThreeDisplays.Config.EMA26_TOTAL_BARS_CHECK + " quotes " + ThreeDisplays.Config.EMA26_ABOVE_BARS + " are not above EMA26 on the long term screen")
            return BlockResult(screen.lastQuote, BlockResultCode.NOT_ALL_NEEDED_QUOTES_ABOUT_EMA_SCREEN_1)
        }

        return BlockResult(screen.lastQuote, BlockResultCode.OK)

    }

}