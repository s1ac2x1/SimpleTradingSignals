package com.kishlaly.ta.analyze.tasks.blocks.two

import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.indicators.ElderForceIndex
import com.kishlaly.ta.model.indicators.Indicator
import com.kishlaly.ta.utils.CollectionUtils
import com.kishlaly.ta.utils.Log

/**
 * third from end is negative
 */
class Long_ScreenTwo_EFI_ThirdNegative : ScreenTwoBlock {
    override fun check(screen: SymbolData): BlockResult {
        val screen_2_EFI = screen.indicator(Indicator.EFI) as List<ElderForceIndex>
        val efi3 = CollectionUtils.getFromEnd<ElderForceIndex>(screen_2_EFI, 3)

        if (efi3.value >= 0) {
            Log.recordCode(BlockResultCode.EFI_3_NOT_NEGATIVE, screen)
            Log.addDebugLine("Third from end EFI is not negative")
            return BlockResult(screen.lastQuote, BlockResultCode.EFI_3_NOT_NEGATIVE)
        }

        return BlockResult(screen.lastQuote, BlockResultCode.OK)
    }
}