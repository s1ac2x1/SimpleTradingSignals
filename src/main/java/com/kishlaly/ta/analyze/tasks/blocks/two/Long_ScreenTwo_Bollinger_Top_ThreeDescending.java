package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.BollingerJava;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.utils.CollectionsTools;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.BOLLINGER_TOP_NOT_DESCENDING_SCREEN_2;
import static com.kishlaly.ta.analyze.BlockResultCode.OK;

/**
 * the last three values of the upper bar are decreasing
 */
public class Long_ScreenTwo_Bollinger_Top_ThreeDescending implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<BollingerJava> screen_2_Bollinger = (List<BollingerJava>) screen.indicators.get(IndicatorJava.BOLLINGER);
        BollingerJava bollinger3 = CollectionsTools.getFromEnd(screen_2_Bollinger, 3);
        BollingerJava bollinger2 = CollectionsTools.getFromEnd(screen_2_Bollinger, 2);
        BollingerJava bollinger1 = CollectionsTools.getFromEnd(screen_2_Bollinger, 1);
        boolean descending = bollinger3.getTop() > bollinger2.getTop() && bollinger2.getTop() > bollinger1.getTop();
        if (!descending) {
            Log.recordCode(BOLLINGER_TOP_NOT_DESCENDING_SCREEN_2, screen);
            Log.addDebugLine("The upper Bollinger band does not narrow on the second screen");
            return new BlockResult(screen.getLastQuote(), BOLLINGER_TOP_NOT_DESCENDING_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
