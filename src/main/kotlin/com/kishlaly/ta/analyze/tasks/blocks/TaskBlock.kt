package com.kishlaly.ta.analyze.tasks.blocks

import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.SymbolData

interface TaskBlock {

    fun check(screen: SymbolData): BlockResult

}