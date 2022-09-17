package com.kishlaly.ta.analyze.tasks.blocks.one

import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.indicators.EMA
import com.kishlaly.ta.model.indicators.Indicator
import com.kishlaly.ta.model.isCrossesEMA
import com.kishlaly.ta.utils.Log

/**
 * the last bar crosses the EMA26
 */
class Long_ScreenOne_EMA_LastBarCrosses : ScreenOneBlock {
    override fun check(screen: SymbolData): BlockResult {
        val screen_1_EMA26 = screen.indicators[Indicator.EMA26] as List<EMA>
        if (!screen.lastQuote.isCrossesEMA(screen_1_EMA26.last().value)) {
            Log.recordCode(BlockResultCode.LAST_QUOTE_NOT_CROSSING_EMA_SCREEN_1, screen)
            Log.addDebugLine("The last bar does not cross the EMA on the long term screen")
            return BlockResult(screen.lastQuote, BlockResultCode.LAST_QUOTE_NOT_CROSSING_EMA_SCREEN_1)
        }

        return BlockResult(screen.lastQuote, BlockResultCode.OK)
    }
}