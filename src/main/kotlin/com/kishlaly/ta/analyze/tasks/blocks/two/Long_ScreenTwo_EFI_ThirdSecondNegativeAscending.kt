package com.kishlaly.ta.analyze.tasks.blocks.two

import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.indicators.ElderForceIndex
import com.kishlaly.ta.model.indicators.Indicator
import com.kishlaly.ta.utils.CollectionUtils
import com.kishlaly.ta.utils.Log

/**
 * the third and second from the end EFI are negative and ascending
 */
class Long_ScreenTwo_EFI_ThirdSecondNegativeAscending : ScreenTwoBlock {
    override fun check(screen: SymbolData): BlockResult {
        val screen_2_EFI = screen.indicator(Indicator.EFI) as List<ElderForceIndex>
        val efi3 = CollectionUtils.getFromEnd<ElderForceIndex>(screen_2_EFI, 3)
        val efi2 = CollectionUtils.getFromEnd<ElderForceIndex>(screen_2_EFI, 2)

        val bothNegative = efi3.value < 0 && efi2.value < 0
        val rising = efi2.value > efi3.value
        val result = bothNegative && rising

        if (!result) {
            Log.recordCode(BlockResultCode.EFI_2_3_NOT_NEGATIVE_AND_ASCENDING, screen)
            Log.addDebugLine("Third and second from the end EFI are not negative and ascending")
            return BlockResult(screen.lastQuote, BlockResultCode.EFI_2_3_NOT_NEGATIVE_AND_ASCENDING)
        }

        return BlockResult(screen.lastQuote, BlockResultCode.OK)
    }
}