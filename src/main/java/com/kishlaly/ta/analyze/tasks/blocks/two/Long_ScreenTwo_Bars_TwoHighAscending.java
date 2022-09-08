package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.utils.LogJava;

import static com.kishlaly.ta.model.BlockResultCodeJava.LAST_QUOTES_NOT_ASCENDING_SCREEN_2;
import static com.kishlaly.ta.model.BlockResultCodeJava.OK;

/**
 * high of the last column above the penultimate one
 */
public class Long_ScreenTwo_Bars_TwoHighAscending implements ScreenTwoBlockJava {
    @Override
    public BlockResultJava check(SymbolDataJava screen) {
        boolean screen_2_check1 = screen.getLastQuote().getHigh() > screen.getPreLastQuote().getHigh();
        if (!screen_2_check1) {
            LogJava.recordCode(LAST_QUOTES_NOT_ASCENDING_SCREEN_2, screen);
            LogJava.addDebugLine("High of the last column is not higher than the penultimate one on the second screen");
            return new BlockResultJava(screen.getLastQuote(), LAST_QUOTES_NOT_ASCENDING_SCREEN_2);
        }
        return new BlockResultJava(screen.getLastQuote(), OK);
    }
}
