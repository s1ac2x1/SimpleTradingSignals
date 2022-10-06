package com.kishlaly.ta.analyze.tasks.blocks.two

import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.indicators.Stochastic
import com.kishlaly.ta.utils.Log
import com.kishlaly.ta.utils.SymbolDataUtils

/**
 * oversold below 20 at TWO values of the slow stochastic line and it goes up
 */
class Long_ScreenTwo_Stoch_D_TwoStrongOversold : ScreenTwoBlock {
    override fun check(screen: SymbolData): BlockResult {
        val stoch = SymbolDataUtils(screen, Stochastic::class.java)
        val oversold = stoch.last(2).slowD < 20 && stoch.last().slowD < 20
        if (!oversold) {
            Log.recordCode(BlockResultCode.STOCH_D_WAS_NOT_STRONG_OVERSOLD_RECENTLY_SCREEN_2, screen)
            Log.addDebugLine("The last two stochastic %D values are at least 20")
            return BlockResult(
                screen.lastQuote,
                BlockResultCode.STOCH_D_WAS_NOT_STRONG_OVERSOLD_RECENTLY_SCREEN_2
            )
        }

        return BlockResult(screen.lastQuote, BlockResultCode.OK)
    }
}