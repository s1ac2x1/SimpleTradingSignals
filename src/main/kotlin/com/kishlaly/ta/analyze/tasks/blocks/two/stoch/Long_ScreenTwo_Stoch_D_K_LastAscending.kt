package com.kishlaly.ta.analyze.tasks.blocks.two.stoch

import com.kishlaly.ta.analyze.tasks.blocks.two.ScreenTwoBlock
import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.indicators.Stochastic
import com.kishlaly.ta.utils.Log
import com.kishlaly.ta.utils.SymbolDataUtils

/**
 * %D and %K of the last stochastic should be higher than that of the penultimate stochastic
 */
class Long_ScreenTwo_Stoch_D_K_LastAscending : ScreenTwoBlock {
    override fun check(screen: SymbolData): BlockResult {
        val stoch = SymbolDataUtils(screen, Stochastic::class.java)
        val ascending = stoch.last().slowK > stoch.last(2).slowK
                && stoch.last().slowD > stoch.last(2).slowD

        if (!ascending) {
            Log.recordCode(BlockResultCode.STOCH_K_D_NOT_ASCENDING_SCREEN_2, screen)
            Log.addDebugLine("Stochastic does not grow on the second screen")
            return BlockResult(screen.lastQuote, BlockResultCode.STOCH_K_D_NOT_ASCENDING_SCREEN_2)
        }

        return BlockResult(screen.lastQuote, BlockResultCode.OK)
    }
}