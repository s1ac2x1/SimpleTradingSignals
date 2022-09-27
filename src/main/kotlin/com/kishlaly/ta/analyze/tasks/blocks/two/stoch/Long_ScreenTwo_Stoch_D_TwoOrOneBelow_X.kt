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
 * one of the two stochastic %D values is less than ThreeDisplays.Config.STOCH_CUSTOM
 */
class Long_ScreenTwo_Stoch_D_TwoOrOneBelow_X : ScreenTwoBlock {
    override fun check(screen: SymbolData): BlockResult {
        if (STOCH_CUSTOM < 0) {
            throw RuntimeException("ThreeDisplays.Config.STOCH_CUSTOM not set")
        }
        val stoch = SymbolDataUtils(screen, Stochastic::class.java)
        val oneBelowExtraLow = stoch.last(2).slowD < STOCH_CUSTOM
                || stoch.last(1).slowD < STOCH_CUSTOM

        if (!oneBelowExtraLow) {
            Log.recordCode(BlockResultCode.STOCH_D_NOT_EXTRA_OVERSOLD_SCREEN_2, screen)
            Log.addDebugLine("One of the last two %D stochastics is not lower than " + ThreeDisplays.Config.STOCH_CUSTOM + " on the second screen")
            return BlockResult(screen.lastQuote, BlockResultCode.STOCH_D_NOT_EXTRA_OVERSOLD_SCREEN_2)
        }

        return BlockResult(screen.lastQuote, BlockResultCode.OK)
    }
}