package com.kishlaly.ta.analyze.tasks.blocks.one;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.utils.Log;

import static com.kishlaly.ta.analyze.BlockResultCode.LAST_QUOTE_NOT_GREEN_SCREEN_1;
import static com.kishlaly.ta.analyze.BlockResultCode.OK;

/**
 * the last quote is green
 */
public class Long_ScreenOne_LastBarGreen implements ScreenOneBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        boolean lastBarIsGreen = screen.getLastQuote().getOpen() < screen.getLastQuote().getClose();
        if (!lastBarIsGreen) {
            Log.recordCode(LAST_QUOTE_NOT_GREEN_SCREEN_1, screen);
            Log.addDebugLine("The last quote is not green on the long-term screen");
            return new BlockResult(screen.getLastQuote(), LAST_QUOTE_NOT_GREEN_SCREEN_1);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
