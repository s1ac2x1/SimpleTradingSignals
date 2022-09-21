package com.kishlaly.ta.analyze.tasks.blocks.commons

import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.Quote
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.utils.Log

class ScreenSoftValidation : CommonBlock {
    override fun check(screen: SymbolData): BlockResult {
        if (screen.isEmptyQuotes || screen.isEmptyIndicators) {
            Log.addDebugLine("There are not enough quotes for ${screen.symbol}")
            Log.recordCode(BlockResultCode.NO_DATA_QUOTES, screen)
            return BlockResult(Quote.NaN(), BlockResultCode.NO_DATA_QUOTES)
        }
        return BlockResult(Quote.NaN(), BlockResultCode.OK)
    }
}