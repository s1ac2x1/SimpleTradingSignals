package com.kishlaly.ta.analyze.tasks.blocks.two

import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.indicators.ElderForceIndex
import com.kishlaly.ta.model.indicators.Indicator
import com.kishlaly.ta.utils.CollectionUtils
import com.kishlaly.ta.utils.Log

/**
 * prelast and last are positive
 */
class Long_ScreenTwo_EFI_TwoPositive : ScreenTwoBlock {
    override fun check(screen: SymbolData): BlockResult {
        val screen_2_EFI = screen.indicator(Indicator.EFI) as List<ElderForceIndex>
        val efi2 = CollectionUtils.getFromEnd<ElderForceIndex>(screen_2_EFI, 2)
        val efi = screen_2_EFI.last()

        val result = efi2.value > 0 && efi.value > 0

        if (!result) {
            Log.recordCode(BlockResultCode.EFI_2_3_NOT_POSITIVE, screen)
            Log.addDebugLine("Prelast and last EFI are not positive")
            return BlockResult(screen.lastQuote, BlockResultCode.EFI_2_3_NOT_POSITIVE)
        }

        return BlockResult(screen.lastQuote, BlockResultCode.OK)
    }
}