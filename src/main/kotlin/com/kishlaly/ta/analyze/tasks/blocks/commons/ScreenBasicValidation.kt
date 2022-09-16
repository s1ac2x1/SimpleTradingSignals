package com.kishlaly.ta.analyze.tasks.blocks.commons

import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.Quote
import com.kishlaly.ta.model.SymbolData

class ScreenBasicValidation : CommonBlock {

    override fun check(screen: SymbolData): BlockResult {
        return BlockResult(Quote.NaN(), BlockResultCode.OK)
    }

}