package com.kishlaly.ta.analyze.tasks.blocks.commons

import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.Quote
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.indicators.Indicator
import com.kishlaly.ta.utils.Log
import com.kishlaly.ta.utils.Quotes.Companion.resolveMinBarsCount
import com.kishlaly.ta.utils.isValid

class ScreenBasicValidation : CommonBlock {

    override fun check(screen: SymbolData): BlockResult {
        if (!screen.isValid()) {
            Log.addDebugLine("There are not enough quotes for ${screen.timeframe.name}")
            Log.recordCode(BlockResultCode.NO_DATA_QUOTES, screen)
            return BlockResult(Quote.NaN(), BlockResultCode.NO_DATA_QUOTES)
        }

        val missingData = mutableListOf<Indicator>()
        screen.indicators.forEach { indicator, value ->
            if (value.isEmpty() || value.size < resolveMinBarsCount(screen.timeframe)) {
                missingData.add(indicator)
            }
        }
        if (!missingData.isEmpty()) {
            Log.recordCode(BlockResultCode.NO_DATA_INDICATORS, screen)
            Log.addDebugLine("No indicator data: " + missingData.map { it.name }.joinToString())
            return BlockResult(Quote.NaN(), BlockResultCode.NO_DATA_INDICATORS)
        }

        return BlockResult(Quote.NaN(), BlockResultCode.OK)
    }

}