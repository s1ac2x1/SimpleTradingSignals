package com.kishlaly.ta.analyze.tasks.blocks.two

import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.indicators.Bollinger
import com.kishlaly.ta.model.indicators.Indicator
import com.kishlaly.ta.utils.CollectionUtils
import com.kishlaly.ta.utils.Log

/**
 * the last three values of the upper bar are decreasing
 */
class Long_ScreenTwo_Bollinger_Top_ThreeDescending : ScreenTwoBlock {
    override fun check(screen: SymbolData): BlockResult {
        val screen_2_Bollinger = screen.indicator(Indicator.BOLLINGER) as List<Bollinger>
        val bollinger3 = CollectionUtils.getFromEnd<Bollinger>(screen_2_Bollinger, 3)
        val bollinger2 = CollectionUtils.getFromEnd<Bollinger>(screen_2_Bollinger, 2)
        val bollinger1 = CollectionUtils.getFromEnd<Bollinger>(screen_2_Bollinger, 1)
        val descending = bollinger3.top > bollinger2.top && bollinger2.top > bollinger1.top
        if (!descending) {
            Log.recordCode(BlockResultCode.BOLLINGER_TOP_NOT_DESCENDING_SCREEN_2, screen)
            Log.addDebugLine("The upper Bollinger band does not narrow on the second screen")
            return BlockResult(screen.lastQuote, BlockResultCode.BOLLINGER_TOP_NOT_DESCENDING_SCREEN_2)
        }

        return BlockResult(screen.lastQuote, BlockResultCode.OK)
    }
}