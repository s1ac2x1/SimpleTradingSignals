package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.utils.Log;

import static com.kishlaly.ta.analyze.BlockResultCode.LAST_QUOTES_NOT_ASCENDING_SCREEN_2;
import static com.kishlaly.ta.analyze.BlockResultCode.OK;

/**
 * high последнего столбика выше предпоследнего
 */
public class Long_ScreenTwo_Bars_TwoHighAscending implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        boolean screen_2_check1 = screen.getLastQuote().getHigh() > screen.getPreLastQuote().getHigh();
        if (!screen_2_check1) {
            Log.recordCode(LAST_QUOTES_NOT_ASCENDING_SCREEN_2, screen);
            Log.addDebugLine("High последнего столбика не выше предпоследнего на втором экране");
            return new BlockResult(screen.getLastQuote(), LAST_QUOTES_NOT_ASCENDING_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
