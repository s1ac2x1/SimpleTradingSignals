package com.kishlaly.ta.analyze.tasks.blocks.two

import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.indicators.ElderForceIndex
import com.kishlaly.ta.model.indicators.Indicator
import com.kishlaly.ta.utils.CollectionUtils
import com.kishlaly.ta.utils.Log

/**
 * last EFI ascending
 */
class Long_ScreenTwo_EFI_LastAscending : ScreenTwoBlock {
    override fun check(screen: SymbolData): BlockResult {
        val screen_2_EFI = screen.indicator(Indicator.EFI) as List<ElderForceIndex>
        val efi2 = CollectionUtils.getFromEnd<ElderForceIndex>(screen_2_EFI, 2)
        val efi1 = screen_2_EFI.last()

        val result = efi1.value > efi2.value

        if (!result) {
            Log.recordCode(BlockResultCode.EFI_LAST_NOT_ASCENDING, screen)
            Log.addDebugLine("Last EFI not ascending")
            return BlockResult(screen.lastQuote, BlockResultCode.EFI_LAST_NOT_ASCENDING)
        }

        return BlockResult(screen.lastQuote, BlockResultCode.OK)
    }
}