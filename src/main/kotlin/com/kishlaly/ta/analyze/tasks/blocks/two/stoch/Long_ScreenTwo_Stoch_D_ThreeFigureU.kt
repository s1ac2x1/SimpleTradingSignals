package com.kishlaly.ta.analyze.tasks.blocks.two.stoch

import com.kishlaly.ta.analyze.tasks.blocks.two.ScreenTwoBlock
import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.indicators.Stochastic
import com.kishlaly.ta.utils.Log
import com.kishlaly.ta.utils.SymbolDataUtils

/**
 * slow stochastic draws the figure U on the last three values
 */
class Long_ScreenTwo_Stoch_D_ThreeFigureU : ScreenTwoBlock {
    override fun check(screen: SymbolData): BlockResult {
        val stoch = SymbolDataUtils(screen, Stochastic::class.java)

        val u = stoch.last(3).slowD > stoch.last(2).slowD
                && stoch.last(2).slowD < stoch.last().slowD
        if (!u) {
            Log.recordCode(BlockResultCode.STOCH_D_NOT_U_SCREEN_2, screen)
            Log.addDebugLine("Stochastic %D does not form a U-shape on the second screen")
            return BlockResult(screen.lastQuote, BlockResultCode.STOCH_D_NOT_U_SCREEN_2)
        }

        return BlockResult(screen.lastQuote, BlockResultCode.OK)
    }
}