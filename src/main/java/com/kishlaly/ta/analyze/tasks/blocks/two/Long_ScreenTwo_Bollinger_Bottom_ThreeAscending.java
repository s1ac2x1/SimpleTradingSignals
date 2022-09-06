package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.BollingerJava;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.utils.CollectionsTools;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.BOLLINGER_BOTTOM_NOT_ASCENDING_SCREEN_2;
import static com.kishlaly.ta.analyze.BlockResultCode.OK;

/**
 * the last three values of the lower band are increasing
 */
public class Long_ScreenTwo_Bollinger_Bottom_ThreeAscending implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<BollingerJava> screen_2_Bollinger = (List<BollingerJava>) screen.indicators.get(Indicator.BOLLINGER);
        BollingerJava bollinger3 = CollectionsTools.getFromEnd(screen_2_Bollinger, 3);
        BollingerJava bollinger2 = CollectionsTools.getFromEnd(screen_2_Bollinger, 2);
        BollingerJava bollinger1 = CollectionsTools.getFromEnd(screen_2_Bollinger, 1);
        boolean ascending = bollinger3.getBottom() < bollinger2.getBottom() && bollinger2.getBottom() < bollinger1.getBottom();
        if (!ascending) {
            Log.recordCode(BOLLINGER_BOTTOM_NOT_ASCENDING_SCREEN_2, screen);
            Log.addDebugLine("The bottom Bollinger band does not narrow on the second screen");
            return new BlockResult(screen.getLastQuote(), BOLLINGER_BOTTOM_NOT_ASCENDING_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
