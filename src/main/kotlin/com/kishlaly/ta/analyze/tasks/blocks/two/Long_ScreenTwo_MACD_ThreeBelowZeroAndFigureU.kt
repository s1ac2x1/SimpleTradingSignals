package com.kishlaly.ta.analyze.tasks.blocks.two

import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.indicators.MACD
import com.kishlaly.ta.utils.Log
import com.kishlaly.ta.utils.SymbolDataUtils

/**
 * histogram should be below zero and start to rise at the last three values
 */
class Long_ScreenTwo_MACD_ThreeBelowZeroAndFigureU : ScreenTwoBlock {
    override fun check(screen: SymbolData): BlockResult {
        val macd = SymbolDataUtils(screen, MACD::class.java)
        val histogramBelowZero = macd.last(3).histogram < 0
                && macd.last(2).histogram < 0
                && macd.last().histogram < 0
        if (!histogramBelowZero) {
            Log.recordCode(BlockResultCode.HISTOGRAM_NOT_BELOW_ZERO_SCREEN_2, screen)
            Log.addDebugLine("The histogram on the second screen is at least zero")
            return BlockResult(screen.lastQuote, BlockResultCode.HISTOGRAM_NOT_BELOW_ZERO_SCREEN_2)
        }

        val figureU = macd.last(2).histogram < macd.last(3).histogram
                && macd.last(2).histogram < macd.last().histogram
        if (!figureU) {
            Log.recordCode(BlockResultCode.HISTOGRAM_NOT_ASCENDING_SCREEN_2, screen)
            Log.addDebugLine("The histogram on the second screen does not form a negative U")
            return BlockResult(screen.lastQuote, BlockResultCode.HISTOGRAM_NOT_ASCENDING_SCREEN_2)
        }
        return BlockResult(screen.lastQuote, BlockResultCode.OK)

    }
}