package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.Bollinger;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.utils.CollectionsTools;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.BOLLINGER_TOP_NOT_DESCENDING_SCREEN_2;
import static com.kishlaly.ta.analyze.BlockResultCode.OK;

/**
 * три последних значений верхней полосы уменьшаются
 */
public class Long_ScreenTwo_Bollinger_Top_ThreeDescending implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<Bollinger> screen_2_Bollinger = (List<Bollinger>) screen.indicators.get(Indicator.BOLLINGER);
        Bollinger bollinger3 = CollectionsTools.getFromEnd(screen_2_Bollinger, 3);
        Bollinger bollinger2 = CollectionsTools.getFromEnd(screen_2_Bollinger, 2);
        Bollinger bollinger1 = CollectionsTools.getFromEnd(screen_2_Bollinger, 1);
        boolean descending = bollinger3.getTop() > bollinger2.getTop() && bollinger2.getTop() > bollinger1.getTop();
        if (!descending) {
            Log.recordCode(BOLLINGER_TOP_NOT_DESCENDING_SCREEN_2, screen);
            Log.addDebugLine("Верхняя лента Боллинжера не сужается на втором экране");
            return new BlockResult(screen.getLastQuote(), BOLLINGER_TOP_NOT_DESCENDING_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
