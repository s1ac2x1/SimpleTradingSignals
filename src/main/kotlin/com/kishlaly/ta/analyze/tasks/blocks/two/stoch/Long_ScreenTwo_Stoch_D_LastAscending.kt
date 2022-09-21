package com.kishlaly.ta.analyze.tasks.blocks.two.stoch

import com.kishlaly.ta.analyze.tasks.blocks.two.ScreenTwoBlock
import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.indicators.Stochastic
import com.kishlaly.ta.utils.Log
import com.kishlaly.ta.utils.SymbolDataUtils

/**
 * the slow line at the right edge is above
 */
class Long_ScreenTwo_Stoch_D_LastAscending : ScreenTwoBlock {
    override fun check(screen: SymbolData): BlockResult {
        val stoch = SymbolDataUtils(screen, Stochastic::class.java)
        val lastStochIsBigger = stoch[1].slowD > stoch[2].slowD
        if (!lastStochIsBigger) {
            Log.recordCode(BlockResultCode.STOCH_K_NOT_ASCENDING_SCREEN_2, screen)
            Log.addDebugLine("The last two %D stochastics do not go up")
            return BlockResult(screen.lastQuote, BlockResultCode.STOCH_K_NOT_ASCENDING_SCREEN_2)
        }
        return BlockResult(screen.lastQuote, BlockResultCode.OK)

    }
}