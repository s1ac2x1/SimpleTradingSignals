package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.Bollinger;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.utils.CollectionsTools;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.*;

/**
 * три последних значений нижней полосы растут
 */
public class Long_ScreenTwo_Bollinger_Bottom_ThreeAscending implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<Bollinger> screen_2_Bollinger = (List<Bollinger>) screen.indicators.get(Indicator.BOLLINGER);
        Bollinger bollinger3 = CollectionsTools.getFromEnd(screen_2_Bollinger, 3);
        Bollinger bollinger2 = CollectionsTools.getFromEnd(screen_2_Bollinger, 2);
        Bollinger bollinger1 = CollectionsTools.getFromEnd(screen_2_Bollinger, 1);
        boolean ascending = bollinger3.getBottom() < bollinger2.getBottom() && bollinger2.getBottom() < bollinger1.getBottom();
        if (!ascending) {
            Log.recordCode(BOLLINGER_BOTTOM_NOT_ASCENDING_SCREEN_2, screen);
            Log.addDebugLine("Нижняя лента Боллинжера не сужается на втором экране");
            return new BlockResult(screen.getLastQuote(), BOLLINGER_BOTTOM_NOT_ASCENDING_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
