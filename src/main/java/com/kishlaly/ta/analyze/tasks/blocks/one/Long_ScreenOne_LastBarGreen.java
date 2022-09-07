package com.kishlaly.ta.analyze.tasks.blocks.one;

import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.utils.LogJava;

import static com.kishlaly.ta.model.BlockResultCodeJava.LAST_QUOTE_NOT_GREEN_SCREEN_1;
import static com.kishlaly.ta.model.BlockResultCodeJava.OK;

/**
 * the last quote is green
 */
public class Long_ScreenOne_LastBarGreen implements ScreenOneBlock {
    @Override
    public BlockResultJava check(SymbolDataJava screen) {
        boolean lastBarIsGreen = screen.getLastQuote().getOpen() < screen.getLastQuote().getClose();
        if (!lastBarIsGreen) {
            LogJava.recordCode(LAST_QUOTE_NOT_GREEN_SCREEN_1, screen);
            LogJava.addDebugLine("The last quote is not green on the long-term screen");
            return new BlockResultJava(screen.getLastQuote(), LAST_QUOTE_NOT_GREEN_SCREEN_1);
        }
        return new BlockResultJava(screen.getLastQuote(), OK);
    }
}
