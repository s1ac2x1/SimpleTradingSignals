package com.kishlaly.ta.analyze.tasks.blocks.two.stoch

import com.kishlaly.ta.analyze.tasks.ThreeDisplays
import com.kishlaly.ta.analyze.tasks.ThreeDisplays.Config.STOCH_CUSTOM
import com.kishlaly.ta.analyze.tasks.blocks.two.ScreenTwoBlock
import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.indicators.Stochastic
import com.kishlaly.ta.utils.Log
import com.kishlaly.ta.utils.SymbolDataUtils

/**
 * the last two %D stochastics below ThreeDisplays.Config.STOCH_CUSTOM
 */
class Long_ScreenTwo_Stoch_D_TwoBelow_X : ScreenTwoBlock {
    override fun check(screen: SymbolData): BlockResult {
        if (STOCH_CUSTOM < 0) {
            throw RuntimeException("ThreeDisplays.Config.STOCH_CUSTOM not set")
        }
        val stoch = SymbolDataUtils(screen, Stochastic::class.java)
        val bothBelowExtraLow = stoch.last(2).slowD < STOCH_CUSTOM
                && stoch.last(1).slowD < STOCH_CUSTOM

        if (!bothBelowExtraLow) {
            Log.recordCode(BlockResultCode.STOCH_D_NOT_EXTRA_OVERSOLD_SCREEN_2, screen)
            Log.addDebugLine("Both last %D stochastics are at least " + ThreeDisplays.Config.STOCH_CUSTOM + " on the second screen")
            return BlockResult(screen.lastQuote, BlockResultCode.STOCH_D_NOT_EXTRA_OVERSOLD_SCREEN_2)
        }

        return BlockResult(screen.lastQuote, BlockResultCode.OK)
    }
}