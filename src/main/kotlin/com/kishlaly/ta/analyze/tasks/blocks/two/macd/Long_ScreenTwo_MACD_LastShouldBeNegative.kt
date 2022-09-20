package com.kishlaly.ta.analyze.tasks.blocks.two.macd

import com.kishlaly.ta.analyze.tasks.blocks.two.ScreenTwoBlock
import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.indicators.Indicator
import com.kishlaly.ta.model.indicators.MACD
import com.kishlaly.ta.utils.Log

class Long_ScreenTwo_MACD_LastShouldBeNegative : ScreenTwoBlock {
    override fun check(screen: SymbolData): BlockResult {
        val screenTwoMacdValues = screen.indicator(Indicator.MACD) as List<MACD>
        if (screenTwoMacdValues.last().histogram > 0) {
            Log.recordCode(BlockResultCode.LAST_HISTOGRAM_ABOVE_ZERO_SCREEN_2, screen)
            Log.addDebugLine("histogram at the right edge above zero")
            return BlockResult(screen.lastQuote, BlockResultCode.LAST_HISTOGRAM_ABOVE_ZERO_SCREEN_2)
        }
        return BlockResult(screen.lastQuote, BlockResultCode.OK)
    }
}