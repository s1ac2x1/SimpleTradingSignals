package com.kishlaly.ta.analyze.tasks.blocks.two.stoch

import com.kishlaly.ta.analyze.tasks.blocks.two.ScreenTwoBlock
import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.indicators.Stochastic
import com.kishlaly.ta.utils.Log
import com.kishlaly.ta.utils.SymbolDataUtils

/**
 * the last %K of the stochastics is above
 */
class Long_ScreenTwo_Stoch_K_TwoAscending : ScreenTwoBlock {
    override fun check(screen: SymbolData): BlockResult {
        val stoch = SymbolDataUtils(screen, Stochastic::class.java)

        val ascendingStochastic = stoch.last(2).slowK < stoch.last(1).slowK
        if (!ascendingStochastic) {
            Log.recordCode(BlockResultCode.STOCH_K_NOT_ASCENDING_SCREEN_2, screen)
            Log.addDebugLine("Stochastic %K does not grow on the second screen")
            return BlockResult(screen.lastQuote, BlockResultCode.STOCH_K_NOT_ASCENDING_SCREEN_2)
        }

        return BlockResult(screen.lastQuote, BlockResultCode.OK)
    }
}