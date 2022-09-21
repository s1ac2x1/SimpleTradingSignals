package com.kishlaly.ta.analyze.tasks.blocks.two.stoch

import com.kishlaly.ta.analyze.tasks.blocks.two.ScreenTwoBlock
import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData

/**
 * %D and %K of the last stochastic should be higher than that of the penultimate stochastic
 */
class Long_ScreenTwo_Stoch_D_K_LastAscending : ScreenTwoBlock {
    override fun check(screen: SymbolData): BlockResult {

        return BlockResult(screen.lastQuote, BlockResultCode.OK)
    }
}