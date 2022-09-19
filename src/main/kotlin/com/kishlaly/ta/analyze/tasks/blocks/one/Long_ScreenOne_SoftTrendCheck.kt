package com.kishlaly.ta.analyze.tasks.blocks.one

import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.indicators.EMA
import com.kishlaly.ta.model.indicators.Indicator
import com.kishlaly.ta.model.isGreen
import com.kishlaly.ta.utils.CollectionUtils
import com.kishlaly.ta.utils.Log

/**
 * the last EMA26 is above
 * the last quote is green
 */
class Long_ScreenOne_SoftTrendCheck : ScreenOneBlock {
    override fun check(screen: SymbolData): BlockResult {
        val screen_1_EMA26 = screen.indicator(Indicator.EMA26) as List<EMA>
        val ema2 = CollectionUtils.getFromEnd(screen_1_EMA26, 2)
        val ema1 = screen_1_EMA26.last()

        val ascending = ema2.value < ema1.value

        if (!ascending) {
            Log.recordCode(BlockResultCode.LAST_EMA_NOT_ASCENDING_SCREEN_1, screen)
            Log.addDebugLine("The last EMA is not higher on the long-term screen")
            return BlockResult(screen.lastQuote, BlockResultCode.LAST_EMA_NOT_ASCENDING_SCREEN_1)
        }

        if (!screen.lastQuote.isGreen()) {
            Log.recordCode(BlockResultCode.QUOTE_NOT_GREEN_SCREEN_1, screen)
            Log.addDebugLine("The last quote is not green on the long-term screen")
            return BlockResult(screen.lastQuote, BlockResultCode.QUOTE_NOT_GREEN_SCREEN_1)
        }

        return BlockResult(screen.lastQuote, BlockResultCode.OK)

    }
}