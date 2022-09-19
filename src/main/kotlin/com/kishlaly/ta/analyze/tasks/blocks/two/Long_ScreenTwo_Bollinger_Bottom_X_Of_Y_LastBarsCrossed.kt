package com.kishlaly.ta.analyze.tasks.blocks.two

import com.kishlaly.ta.analyze.tasks.ThreeDisplays
import com.kishlaly.ta.analyze.tasks.ThreeDisplays.Config.BOLLINGER_CROSSED_BOTTOM_BARS
import com.kishlaly.ta.analyze.tasks.ThreeDisplays.Config.BOLLINGER_TOTAL_BARS_CHECK
import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.indicators.Bollinger
import com.kishlaly.ta.model.indicators.Indicator
import com.kishlaly.ta.model.isCrossesBollingerBottom
import com.kishlaly.ta.utils.Log

/**
 * X out of the last Y bars touched the bottom Bollinger band
 */
class Long_ScreenTwo_Bollinger_Bottom_X_Of_Y_LastBarsCrossed : ScreenTwoBlock {
    override fun check(screen: SymbolData): BlockResult {
        if (BOLLINGER_TOTAL_BARS_CHECK < 0 || BOLLINGER_CROSSED_BOTTOM_BARS < 0) {
            println("BOLLINGER_TOTAL_BARS_CHECK or BOLLINGER_CROSSED_BOTTOM_BARS were not defined")
            return BlockResult.configurationError(screen.lastQuote)
        }
        val screen_2_Bollinger = screen.indicator(Indicator.BOLLINGER) as List<Bollinger>
        var crossed = 0
        for (i in (screen_2_Bollinger.size - BOLLINGER_TOTAL_BARS_CHECK) until screen_2_Bollinger.size) {
            if (screen.quote(i).isCrossesBollingerBottom(screen_2_Bollinger[i])) {
                crossed++
            }
        }
        if (crossed < ThreeDisplays.Config.BOLLINGER_CROSSED_BOTTOM_BARS) {
            Log.recordCode(BlockResultCode.LAST_QUOTE_NOT_CROSSED_BOLLINGER_BOTTOM_SCREEN_2, screen)
            Log.addDebugLine("X out of the last Y bars did not touch the bottom Bollinger band")
            return BlockResult(
                screen.lastQuote,
                BlockResultCode.LAST_QUOTE_NOT_CROSSED_BOLLINGER_BOTTOM_SCREEN_2
            )
        }

        return BlockResult(screen.lastQuote, BlockResultCode.OK)
    }
}