package com.kishlaly.ta.analyze.tasks.blocks.two.macd

import com.kishlaly.ta.analyze.tasks.blocks.two.ScreenTwoBlock
import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.utils.Log
import com.kishlaly.ta.utils.MACDUtils

/**
 * histogram should be below zero and start to rise at the last three values
 */
class Long_ScreenTwo_MACD_ThreeBelowZeroAndAscending : ScreenTwoBlock {
    override fun check(screen: SymbolData): BlockResult {
        val utils = MACDUtils(screen)
        val histogramBelowZero = utils.last(3).histogram < 0
                && utils.last(2).histogram < 0
                && utils.last(1).histogram < 0
        if (!histogramBelowZero) {
            Log.recordCode(BlockResultCode.HISTOGRAM_NOT_BELOW_ZERO_SCREEN_2, screen)
            Log.addDebugLine("The bar graph on the second screen is at least zero")
            return BlockResult(screen.lastQuote, BlockResultCode.HISTOGRAM_NOT_BELOW_ZERO_SCREEN_2)
        }

        val ascendingHistogram = utils.last(3).histogram < utils.last(2).histogram
                && utils.last(2).histogram < utils.last(1).histogram
        if (!ascendingHistogram) {
            Log.recordCode(BlockResultCode.HISTOGRAM_NOT_ASCENDING_SCREEN_2, screen)
            Log.addDebugLine("The histogram on the second screen does not increase")
            return BlockResult(screen.lastQuote, BlockResultCode.HISTOGRAM_NOT_ASCENDING_SCREEN_2)
        }

        return BlockResult(screen.lastQuote, BlockResultCode.OK)
    }
}