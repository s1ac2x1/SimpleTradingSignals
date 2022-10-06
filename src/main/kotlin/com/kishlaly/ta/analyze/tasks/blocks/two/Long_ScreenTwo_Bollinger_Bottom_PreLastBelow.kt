package com.kishlaly.ta.analyze.tasks.blocks.two

import com.kishlaly.ta.analyze.tasks.blocks.two.ScreenTwoBlock
import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.belowBollingerBottom
import com.kishlaly.ta.model.indicators.Bollinger
import com.kishlaly.ta.model.indicators.Indicator
import com.kishlaly.ta.utils.CollectionUtils
import com.kishlaly.ta.utils.Log

/**
 * the second quote from the end below the bottom Bollinger band
 */
class Long_ScreenTwo_Bollinger_Bottom_PreLastBelow : ScreenTwoBlock {
    override fun check(screen: SymbolData): BlockResult {
        val screen_2_Bollinger = screen.indicator(Indicator.BOLLINGER) as List<Bollinger>
        val preLastBollinger = CollectionUtils.getFromEnd(screen_2_Bollinger, 2)

        if (!(screen.preLastQuote belowBollingerBottom preLastBollinger)) {
            Log.recordCode(BlockResultCode.QUOTE_2_NOT_BELOW_BOLLINGER_BOTTOM_SCREEN_2, screen)
            Log.addDebugLine("The second from the end of the quote is not lower than the bottom Bollinger band on the second screen")
            return BlockResult(
                screen.lastQuote,
                BlockResultCode.QUOTE_2_NOT_BELOW_BOLLINGER_BOTTOM_SCREEN_2
            )
        }
        return BlockResult(screen.lastQuote, BlockResultCode.OK)
    }
}