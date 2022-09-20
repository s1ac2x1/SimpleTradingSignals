package com.kishlaly.ta.analyze.tasks.blocks.two.keltner

import com.kishlaly.ta.analyze.tasks.ThreeDisplays
import com.kishlaly.ta.analyze.tasks.blocks.two.ScreenTwoBlock
import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.crossesKeltnerBottom
import com.kishlaly.ta.model.indicators.Indicator
import com.kishlaly.ta.model.indicators.Keltner
import com.kishlaly.ta.utils.Log

class Long_ScreenTwo_Keltner_Bottom_X_FromEndCrossed : ScreenTwoBlock {
    override fun check(screen: SymbolData): BlockResult {
        if (ThreeDisplays.Config.QUOTE_FROM_END_TO_USE < 0) {
            throw RuntimeException("QUOTE_FROM_END_TO_USE not defined")
        }
        val screen_2_Keltner = screen.indicator(Indicator.KELTNER) as List<Keltner>
        val keltner = screen_2_Keltner[screen_2_Keltner.size - 1 - ThreeDisplays.Config.QUOTE_FROM_END_TO_USE]
        val quote = screen.quote(screen.quotesCount - 1 - ThreeDisplays.Config.QUOTE_FROM_END_TO_USE)

        if (!(quote crossesKeltnerBottom keltner)) {
            Log.addDebugLine(ThreeDisplays.Config.QUOTE_FROM_END_TO_USE.toString() + " quote from the end the has not crossed the lower boundary of the Keltner channel")
            Log.recordCode(BlockResultCode.X_FROM_END_QUOTE_DIDNT_CROSSED_KELTNER_BOTTOM_SCREEN_2, screen)
            return BlockResult(screen.lastQuote, BlockResultCode.X_FROM_END_QUOTE_DIDNT_CROSSED_KELTNER_BOTTOM_SCREEN_2)
        }

        return BlockResult(screen.lastQuote, BlockResultCode.OK)
    }
}