package com.kishlaly.ta.analyze.tasks.blocks.two.stoch

import com.kishlaly.ta.analyze.tasks.blocks.two.ScreenTwoBlock
import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.indicators.Stochastic
import com.kishlaly.ta.utils.Log
import com.kishlaly.ta.utils.SymbolDataUtils

/**
 * the last three %D stochastics are rising
 */
class Long_ScreenTwo_Stoch_D_ThreeAscending : ScreenTwoBlock {
    override fun check(screen: SymbolData): BlockResult {
        val stoch = SymbolDataUtils(screen, Stochastic::class.java)

        // it is enough that the last one is larger than the last two
        val ascendingStochastic = stoch.last(1).slowD > stoch.last(2).slowD
                && stoch.last(1).slowD > stoch.last(3).slowD
        if (!ascendingStochastic) {
            Log.recordCode(BlockResultCode.STOCH_K_NOT_ASCENDING_SCREEN_2, screen)
            Log.addDebugLine("Stochastic %D does not grow on the second screen")
            return BlockResult(screen.lastQuote, BlockResultCode.STOCH_K_NOT_ASCENDING_SCREEN_2)
        }

        return BlockResult(screen.lastQuote, BlockResultCode.OK)
    }
}