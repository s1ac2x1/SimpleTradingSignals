package com.kishlaly.ta.analyze.tasks.blocks.two

import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.indicators.ElderForceIndex
import com.kishlaly.ta.model.indicators.Indicator
import com.kishlaly.ta.utils.Log

/**
 * last EFI is positive
 */
class Long_ScreenTwo_EFI_LastPositive : ScreenTwoBlock {
    override fun check(screen: SymbolData): BlockResult {
        val screen_2_EFI = screen.indicator(Indicator.EFI) as List<ElderForceIndex>
        val efi1 = screen_2_EFI.last()

        val result = efi1.value > 0

        if (!result) {
            Log.recordCode(BlockResultCode.EFI_LAST_NOT_POSITIVE, screen)
            Log.addDebugLine("Last EFI not positive")
            return BlockResult(screen.lastQuote, BlockResultCode.EFI_LAST_NOT_POSITIVE)
        }

        return BlockResult(screen.lastQuote, BlockResultCode.OK)
    }
}