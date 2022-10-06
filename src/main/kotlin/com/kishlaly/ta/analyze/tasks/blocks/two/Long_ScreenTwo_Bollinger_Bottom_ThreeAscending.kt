package com.kishlaly.ta.analyze.tasks.blocks.two

import com.kishlaly.ta.analyze.tasks.blocks.two.ScreenTwoBlock
import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.indicators.Bollinger
import com.kishlaly.ta.model.indicators.Indicator
import com.kishlaly.ta.utils.CollectionUtils
import com.kishlaly.ta.utils.Log

/**
 * the last three values of the lower band are increasing
 */
class Long_ScreenTwo_Bollinger_Bottom_ThreeAscending : ScreenTwoBlock {
    override fun check(screen: SymbolData): BlockResult {
        val screen_2_Bollinger = screen.indicator(Indicator.BOLLINGER) as List<Bollinger>
        val bollinger3 = CollectionUtils.getFromEnd<Bollinger>(screen_2_Bollinger, 3)
        val bollinger2 = CollectionUtils.getFromEnd<Bollinger>(screen_2_Bollinger, 2)
        val bollinger1 = CollectionUtils.getFromEnd<Bollinger>(screen_2_Bollinger, 1)
        val ascending = bollinger3.bottom < bollinger2.bottom
                && bollinger2.bottom < bollinger1.bottom
        if (!ascending) {
            Log.recordCode(BlockResultCode.BOLLINGER_BOTTOM_NOT_ASCENDING_SCREEN_2, screen)
            Log.addDebugLine("The last three values of the lower band are not increasing")
            return BlockResult(screen.lastQuote, BlockResultCode.BOLLINGER_BOTTOM_NOT_ASCENDING_SCREEN_2)
        }
        return BlockResult(screen.lastQuote, BlockResultCode.OK)
    }
}