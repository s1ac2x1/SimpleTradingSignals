package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.model.indicators.BollingerJava;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.utils.CollectionUtilsJava;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.model.BlockResultCodeJava.BOLLINGER_BOTTOM_NOT_ASCENDING_SCREEN_2;
import static com.kishlaly.ta.model.BlockResultCodeJava.OK;

/**
 * the last three values of the lower band are increasing
 */
public class Long_ScreenTwo_Bollinger_Bottom_ThreeAscending implements ScreenTwoBlock {
    @Override
    public BlockResultJava check(SymbolDataJava screen) {
        List<BollingerJava> screen_2_Bollinger = (List<BollingerJava>) screen.indicators.get(IndicatorJava.BOLLINGER);
        BollingerJava bollinger3 = CollectionUtilsJava.getFromEnd(screen_2_Bollinger, 3);
        BollingerJava bollinger2 = CollectionUtilsJava.getFromEnd(screen_2_Bollinger, 2);
        BollingerJava bollinger1 = CollectionUtilsJava.getFromEnd(screen_2_Bollinger, 1);
        boolean ascending = bollinger3.getBottom() < bollinger2.getBottom() && bollinger2.getBottom() < bollinger1.getBottom();
        if (!ascending) {
            Log.recordCode(BOLLINGER_BOTTOM_NOT_ASCENDING_SCREEN_2, screen);
            Log.addDebugLine("The bottom Bollinger band does not narrow on the second screen");
            return new BlockResultJava(screen.getLastQuote(), BOLLINGER_BOTTOM_NOT_ASCENDING_SCREEN_2);
        }
        return new BlockResultJava(screen.getLastQuote(), OK);
    }
}
