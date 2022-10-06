package com.kishlaly.ta.analyze.tasks.blocks.two

import com.kishlaly.ta.analyze.tasks.ThreeDisplays
import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.indicators.Indicator
import com.kishlaly.ta.model.indicators.Keltner
import com.kishlaly.ta.utils.Log

class Long_ScreenTwo_FilterLateEntry : ScreenTwoBlock {
    override fun check(screen: SymbolData): BlockResult {
        val screen_2_Keltner = screen.indicator(Indicator.KELTNER) as List<Keltner>
        if (ThreeDisplays.Config.FILTER_BY_KELTNER_ENABLED) {
            val middle = screen_2_Keltner.last().middle
            val top = screen_2_Keltner.last().top
            val diff = top - middle
            val ratio = diff / 100 * ThreeDisplays.Config.FILTER_BY_KELTNER
            val maxAllowedCloseValue = middle + ratio
            if (screen.lastQuote.close >= maxAllowedCloseValue) {
                Log.addDebugLine("The last quote closed above " + ThreeDisplays.Config.FILTER_BY_KELTNER + "% of the distance from the middle to the top of the channel")
                Log.recordCode(BlockResultCode.QUOTE_CLOSED_ABOVE_KELTNER_RULE_SCREEN_2, screen)
                return BlockResult(
                    screen.lastQuote, BlockResultCode.QUOTE_CLOSED_ABOVE_KELTNER_RULE_SCREEN_2
                )
            }
        }
        return BlockResult(screen.lastQuote, BlockResultCode.OK)

    }
}