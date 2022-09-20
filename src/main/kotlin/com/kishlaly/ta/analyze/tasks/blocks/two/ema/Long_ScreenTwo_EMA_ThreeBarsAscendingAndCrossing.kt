package com.kishlaly.ta.analyze.tasks.blocks.two.ema

import com.kishlaly.ta.analyze.tasks.blocks.two.ScreenTwoBlock
import com.kishlaly.ta.model.*
import com.kishlaly.ta.model.indicators.EMA
import com.kishlaly.ta.model.indicators.Indicator
import com.kishlaly.ta.utils.CollectionUtils
import com.kishlaly.ta.utils.Log

/**
 * price bars should cross EMA13 and should go up
 */
class Long_ScreenTwo_EMA_ThreeBarsAscendingAndCrossing : ScreenTwoBlock {
    override fun check(screen: SymbolData): BlockResult {
        val quote3 = CollectionUtils.getFromEnd<Quote>(screen.allQuotes, 3)
        val quote2 = screen.preLastQuote
        val quote1 = screen.lastQuote

        val screen_2_EMA13 = screen.indicator(Indicator.EMA13) as List<EMA>
        val ema3 = CollectionUtils.getFromEnd<EMA>(screen_2_EMA13, 3)
        val ema2 = CollectionUtils.getFromEnd<EMA>(screen_2_EMA13, 1)
        val ema1 = screen_2_EMA13.last()

        // prerequisite 1:
        // make sure first that the last three columns increase

        val ascendingBarHigh = quote3.high < quote2.high && quote2.high < quote1.high
        val ascendingBarClose = quote3.close < quote2.close && quote2.close < quote1.close

        if (!ascendingBarHigh) {
            Log.recordCode(BlockResultCode.QUOTE_HIGH_NOT_GROWING_SCREEN_2, screen)
            Log.addDebugLine("Quote.high не растет последовательно")
            if (!ascendingBarClose) {
                Log.recordCode(BlockResultCode.QUOTE_CLOSE_NOT_GROWING_SCREEN_2, screen)
                Log.addDebugLine("Quote.close does not grow consistently")
                // the third from the end all below EMA13, and the second and last crossed
                val crossingRule = quote3 below ema3 && (quote2.crosses(ema2) || quote1.crosses(ema1))
                if (!crossingRule) {
                    Log.addDebugLine("Third from the end" + (if (quote3 below ema3) " " else " not ") + "below ЕМА13")
                    Log.addDebugLine("Penultimate" + (if (quote2.crosses(ema2)) " " else " not ") + "crossed ЕМА13")
                    Log.addDebugLine("Last" + (if (quote1.crosses(ema1)) " " else " not ") + "crossed ЕМА13")
                    Log.recordCode(BlockResultCode.CROSSING_RULE_VIOLATED_SCREEN_2, screen)
                    return BlockResult(screen.lastQuote, BlockResultCode.CROSSING_RULE_VIOLATED_SCREEN_2)
                } else {
                    Log.recordCode(BlockResultCode.CROSSING_RULE_PASSED_SCREEN_2, screen)
                    Log.addDebugLine("The intersection rule is satisfied")
                }

            } else {
                Log.recordCode(BlockResultCode.QUOTE_CLOSE_GROWING_SCREEN_2, screen)
                Log.addDebugLine("There is an increase in Quote.close")
            }
        } else {
            Log.recordCode(BlockResultCode.QUOTE_HIGH_GROWING_SCREEN_2, screen)
            Log.addDebugLine("There is a rise in Quote.high")
        }

        return BlockResult(screen.lastQuote, BlockResultCode.OK)
    }
}