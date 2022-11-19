package com.kishlaly.ta.analyze.tasks.blocks.one

import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.indicators.EMA
import com.kishlaly.ta.model.indicators.Indicator
import com.kishlaly.ta.utils.CollectionUtils
import com.kishlaly.ta.utils.Log

/**
 * the last EMA increasing
 */
class Long_ScreenOne_EMA_LastAscending : ScreenOneBlock {
    override fun check(screen: SymbolData): BlockResult {
        val screen_1_EMA26 = screen.indicator(Indicator.EMA26) as List<EMA>
        val ema2 = CollectionUtils.getFromEnd<EMA>(screen_1_EMA26, 2)
        val ema1 = CollectionUtils.getFromEnd<EMA>(screen_1_EMA26, 1)

        val ascending = ema2.value < ema1.value

        if (!ascending) {
            Log.recordCode(BlockResultCode.EMA_NOT_ASCENDING_SCREEN_1, screen)
            Log.addDebugLine("Last EMA do not grow on the long-term screen")
            return BlockResult(screen.lastQuote, BlockResultCode.EMA_NOT_ASCENDING_SCREEN_1)
        }

        return BlockResult(screen.lastQuote, BlockResultCode.OK)
    }
}