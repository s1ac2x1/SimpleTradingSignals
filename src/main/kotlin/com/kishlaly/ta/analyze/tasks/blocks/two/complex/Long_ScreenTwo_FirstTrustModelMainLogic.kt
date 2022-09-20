package com.kishlaly.ta.analyze.tasks.blocks.two.complex

import com.kishlaly.ta.analyze.tasks.FirstTrustModel
import com.kishlaly.ta.analyze.tasks.blocks.two.ScreenTwoBlock
import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.Quote
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.utils.Log

class Long_ScreenTwo_FirstTrustModelMainLogic : ScreenTwoBlock {
    override fun check(screen: SymbolData): BlockResult {
        val signal = screen.lastQuote

        // look for the minimum for the last MONTHS months in one of the last 10 columns
        val days = FirstTrustModel.Config.MONTHS * 21
        val nMonthsLow = screen.allQuotes.subList(screen.quotesCount - days, screen.quotesCount)
            .minWith(compareBy { it.low })
        var nMonthsLowIndex = -1
        for (i in screen.allQuotes.indices) {
            if (screen.quote(i).timestamp.compareTo(nMonthsLow.timestamp) == 0) {
                nMonthsLowIndex = i
                break
            }
        }
        if (nMonthsLowIndex < 0) {
            Log.addDebugLine("Not enough price bars to find a six-month low at " + screen.symbol)
            Log.recordCode(BlockResultCode.NO_DATA_QUOTES, screen)
            return BlockResult(screen.lastQuote, BlockResultCode.NO_DATA_QUOTES)
        }

        if (screen.quotesCount - nMonthsLowIndex > 5) {
            Log.addDebugLine("The minimum is found far from the last three bars")
            Log.recordCode(BlockResultCode.N_MONTHS_LOW_IS_TOO_FAR_SCREEN_2, screen)
            return BlockResult(screen.lastQuote, BlockResultCode.N_MONTHS_LOW_IS_TOO_FAR_SCREEN_2)
        }

        if (nMonthsLowIndex + 2 >= screen.quotesCount) {
            Log.addDebugLine("Minimum detected too close to the right edge")
            Log.recordCode(BlockResultCode.N_MONTHS_LOW_IS_TOO_CLOSE_SCREEN_2, screen)
            return BlockResult(screen.lastQuote, BlockResultCode.N_MONTHS_LOW_IS_TOO_CLOSE_SCREEN_2)
        }

        // looking for at least two green bars after the minimum
        val quote_1_afterMin: Quote = screen.quote(screen.quotesCount - nMonthsLowIndex + 1)
        val quote_2_afterMin: Quote = screen.quote(screen.quotesCount - nMonthsLowIndex + 2)
        val ascendingLastBars =
            quote_1_afterMin.open < quote_1_afterMin.close && quote_2_afterMin.open < quote_2_afterMin.close
        if (!ascendingLastBars) {
            Log.addDebugLine("After the minimum there was no growth of two bars")
            Log.recordCode(BlockResultCode.QUOTES_NOT_ASCENDING_AFTER_MIN, screen)
            return BlockResult(screen.lastQuote, BlockResultCode.QUOTES_NOT_ASCENDING_AFTER_MIN)
        }

        return BlockResult(signal, BlockResultCode.OK)

    }
}