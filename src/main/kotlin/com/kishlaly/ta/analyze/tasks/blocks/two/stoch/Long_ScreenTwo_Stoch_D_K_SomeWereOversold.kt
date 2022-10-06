package com.kishlaly.ta.analyze.tasks.blocks.two.stoch

import com.kishlaly.ta.analyze.tasks.ThreeDisplays
import com.kishlaly.ta.analyze.tasks.ThreeDisplays.Config.STOCH_OVERSOLD
import com.kishlaly.ta.analyze.tasks.ThreeDisplays.Config.STOCH_VALUES_TO_CHECK
import com.kishlaly.ta.analyze.tasks.blocks.two.ScreenTwoBlock
import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.indicators.Stochastic
import com.kishlaly.ta.utils.Log
import com.kishlaly.ta.utils.SymbolDataUtils

/**
 * to check several stochastics to the left of the last value
 * e.g. the last STOCH_VALUES_TO_CHECK: if among them there are values below STOCH_OVERSOLD
 */
class Long_ScreenTwo_Stoch_D_K_SomeWereOversold : ScreenTwoBlock {
    override fun check(screen: SymbolData): BlockResult {
        val stoch = SymbolDataUtils(screen, Stochastic::class.java)
        var wasOversoldRecently = false
        for (i in stoch.size() - STOCH_VALUES_TO_CHECK until stoch.size()) {
            if (stoch[i].slowD <= STOCH_OVERSOLD || stoch[i].slowK <= STOCH_OVERSOLD) {
                wasOversoldRecently = true
                break
            }
        }
        if (!wasOversoldRecently) {
            Log.recordCode(BlockResultCode.STOCH_D_K_WAS_NOT_OVERSOLD_RECENTLY_SCREEN_2, screen)
            Log.addDebugLine("Stochastic was not oversold on the last " + ThreeDisplays.Config.STOCH_VALUES_TO_CHECK + " values")
            return BlockResult(
                screen.lastQuote,
                BlockResultCode.STOCH_D_K_WAS_NOT_OVERSOLD_RECENTLY_SCREEN_2
            )
        }
        return BlockResult(screen.lastQuote, BlockResultCode.OK)

    }
}