package com.kishlaly.ta.analyze.tasks.blocks.one

import com.kishlaly.ta.analyze.tasks.ThreeDisplays
import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.indicators.EMA
import com.kishlaly.ta.model.indicators.Indicator

/**
 * of the latest {TOTAL} quotes at least {ABOVE} is completely above EMA26
 */
class Long_ScreenOne_EMA_X_OutOf_Y_Above : ScreenOneBlock {

    override fun check(screen: SymbolData): BlockResult {
        if (ThreeDisplays.Config.EMA26_TOTAL_BARS_CHECK < 0) {
            throw RuntimeException("ThreeDisplays.Config.EMA26_TOTAL_BARS_CHECK not set")
        }
        if (ThreeDisplays.Config.EMA26_ABOVE_BARS < 0)
            throw RuntimeException("ThreeDisplays.Config.EMA26_ABOVE_BARS not set")
        }

        val screen_1_EMA26 = screen.indicator(Indicator.EMA26) as List<EMA>

    }

}