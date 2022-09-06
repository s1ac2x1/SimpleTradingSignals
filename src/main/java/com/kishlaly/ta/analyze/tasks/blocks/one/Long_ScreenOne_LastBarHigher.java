package com.kishlaly.ta.analyze.tasks.blocks.one;

import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.utils.Log;

import static com.kishlaly.ta.model.BlockResultCodeJava.LAST_QUOTES_NOT_ASCENDING_SCREEN_1;
import static com.kishlaly.ta.model.BlockResultCodeJava.OK;

/**
 * the last quote is higher than the penultimate one
 */
public class Long_ScreenOne_LastBarHigher implements ScreenOneBlock {
    @Override
    public BlockResultJava check(SymbolData screen) {
        boolean lastBarHigher = screen.getLastQuote().getLow() > screen.getPreLastQuote().getLow()
                && screen.getLastQuote().getHigh() > screen.getPreLastQuote().getHigh();

        if (!lastBarHigher) {
            Log.recordCode(LAST_QUOTES_NOT_ASCENDING_SCREEN_1, screen);
            Log.addDebugLine("The last bar is not higher than the penultimate on the long-term screen");
            return new BlockResultJava(screen.getPreLastQuote(), LAST_QUOTES_NOT_ASCENDING_SCREEN_1);
        }
        return new BlockResultJava(screen.getPreLastQuote(), OK);
    }
}
