package com.kishlaly.ta.analyze.tasks.blocks.two

import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.indicators.Indicator
import com.kishlaly.ta.model.indicators.MACD
import com.kishlaly.ta.utils.CollectionUtils
import com.kishlaly.ta.utils.Log

/**
 * the histogram rises on the last three values
 */
class Long_ScreenTwo_MACD_ThreeAscending : ScreenTwoBlock {
    override fun check(screen: SymbolData): BlockResult {
        val screen_2_MACD = screen.indicator(Indicator.MACD) as List<MACD>
        val macd3 = CollectionUtils.getFromEnd<MACD>(screen_2_MACD, 3).histogram
        val macd2 = CollectionUtils.getFromEnd<MACD>(screen_2_MACD, 2).histogram
        val macd1 = screen_2_MACD.last().histogram

        val ascending = macd3 < macd2 && macd2 < macd1
        if (!ascending) {
            Log.recordCode(BlockResultCode.HISTOGRAM_NOT_ASCENDING_SCREEN_2, screen)
            Log.addDebugLine("The histogram on the second screen does not increase")
            return BlockResult(screen.lastQuote, BlockResultCode.HISTOGRAM_NOT_ASCENDING_SCREEN_2)
        }
        return BlockResult(screen.lastQuote, BlockResultCode.OK)
    }
}