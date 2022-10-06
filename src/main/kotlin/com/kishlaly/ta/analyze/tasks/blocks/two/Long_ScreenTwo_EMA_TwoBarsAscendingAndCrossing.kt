package com.kishlaly.ta.analyze.tasks.blocks.two

import com.kishlaly.ta.analyze.tasks.blocks.two.ScreenTwoBlock
import com.kishlaly.ta.model.*
import com.kishlaly.ta.model.indicators.EMA
import com.kishlaly.ta.model.indicators.Indicator
import com.kishlaly.ta.utils.CollectionUtils
import com.kishlaly.ta.utils.Log

/**
 * Quotes should cross EMA13 and should go up
 */
class Long_ScreenTwo_EMA_TwoBarsAscendingAndCrossing : ScreenTwoBlock {
    override fun check(screen: SymbolData): BlockResult {
        val screen_2_EMA13 = screen.indicator(Indicator.EMA13) as List<EMA>

        // prerequisite 1:
        // make sure first that the last TWO columns go up
        val ascendingBarHigh = screen.preLastQuote.high < screen.lastQuote.high
        if (!ascendingBarHigh) {
            Log.recordCode(BlockResultCode.QUOTE_HIGH_NOT_GROWING_SCREEN_2, screen)
            Log.addDebugLine("Quote.high does not grow consistently")
            return BlockResult(screen.lastQuote, BlockResultCode.QUOTE_HIGH_NOT_GROWING_SCREEN_2)
        }

        val preLastEMA = CollectionUtils.getFromEnd<EMA>(screen_2_EMA13, 2)
        val lastEMA = CollectionUtils.getFromEnd<EMA>(screen_2_EMA13, 1)

        // both quotes below EMA - reject
        if (screen.preLastQuote below preLastEMA && screen.lastQuote below lastEMA) {
            Log.addDebugLine("Both last two bars are below the EMA")
            Log.recordCode(BlockResultCode.QUOTES_BELOW_EMA_SCREEN_2, screen)
            return BlockResult(screen.lastQuote, BlockResultCode.QUOTES_BELOW_EMA_SCREEN_2)
        }

        // both quotes above the EMA - reject
        if (screen.preLastQuote above preLastEMA && screen.lastQuote above lastEMA) {
            Log.addDebugLine("Both last two bars are above the EMA")
            Log.recordCode(BlockResultCode.QUOTES_ABOVE_EMA_SCREEN_2, screen)
            return BlockResult(screen.lastQuote, BlockResultCode.QUOTES_ABOVE_EMA_SCREEN_2)
        }

        // the penultimate one is below the EMA, the last one crosses or is above it - OK
        val crossingRule1 =
            screen.preLastQuote below preLastEMA && (screen.lastQuote crosses lastEMA || screen.lastQuote above lastEMA)

        // the penultimate one is below the EMA, the last one crosses - OK
        val crossingRule2 = screen.preLastQuote below preLastEMA && screen.lastQuote crosses lastEMA

        // penultimate and last cross the EMA - OK
        val crossingRule3 = screen.preLastQuote crosses preLastEMA && screen.lastQuote crosses lastEMA

        // the penultimate one crosses the EMA, the last one is higher (it may be too late to enter the trade, we need to look at the chart) - OK
        val crossingRule4 = screen.preLastQuote crosses preLastEMA && screen.lastQuote above lastEMA

        val crossingOk = crossingRule1 || crossingRule2 || crossingRule3 || crossingRule4
        if (!crossingOk) {
            Log.addDebugLine("The rule of EMA crossing is not fulfilled")
            Log.recordCode(BlockResultCode.CROSSING_RULE_VIOLATED_SCREEN_2, screen)
            return BlockResult(screen.lastQuote, BlockResultCode.CROSSING_RULE_VIOLATED_SCREEN_2)
        }

        return BlockResult(screen.lastQuote, BlockResultCode.OK)
    }
}