package com.kishlaly.ta.analyze.tasks.blocks.two

import com.kishlaly.ta.analyze.tasks.ThreeDisplays
import com.kishlaly.ta.analyze.tasks.ThreeDisplays.Config.STOCH_OVERSOLD
import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.indicators.Stochastic
import com.kishlaly.ta.utils.Log
import com.kishlaly.ta.utils.SymbolDataUtils

class Long_ScreenTwo_Stoch_D_K_ThreeAscendingFromOversold : ScreenTwoBlock {
    override fun check(screen: SymbolData): BlockResult {
        val stoch = SymbolDataUtils(screen, Stochastic::class.java)

        // the third or second from the end of %K below STOCH_OVERSOLD, and the very last above the third
        val isOversoldK = (stoch.last(3).slowK <= STOCH_OVERSOLD || stoch.last(2).slowK <= STOCH_OVERSOLD)
                && stoch.last().slowK > stoch.last(3).slowK

        // the third or second from the end of %D below STOCH_OVERSOLD, and the very last above both
        val isOversoldD = (stoch.last(3).slowD <= STOCH_OVERSOLD || stoch.last(2).slowD <= STOCH_OVERSOLD)
                && stoch.last().slowD > stoch.last(2).slowD && stoch.last().slowD > stoch.last(3).slowD

        if (!isOversoldK || !isOversoldD) {
            Log.recordCode(BlockResultCode.STOCH_D_K_NOT_ASCENDING_FROM_OVERSOLD_SCREEN_2, screen)
            Log.addDebugLine("Stochastic does not rise from oversold " + ThreeDisplays.Config.STOCH_OVERSOLD + ". %D: " + isOversoldD + "; %K: " + isOversoldK)
            return BlockResult(
                screen.lastQuote, BlockResultCode.STOCH_D_K_NOT_ASCENDING_FROM_OVERSOLD_SCREEN_2
            )
        }

        return BlockResult(screen.lastQuote, BlockResultCode.OK)
    }
}