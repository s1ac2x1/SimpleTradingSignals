package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.model.indicators.KeltnerJava;
import com.kishlaly.ta.utils.CollectionsTools;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.model.BlockResultCodeJava.OK;
import static com.kishlaly.ta.model.BlockResultCodeJava.QUOTE_CLOSED_ABOVE_KELTNER_RULE_SCREEN_2;
import static com.kishlaly.ta.analyze.tasks.ThreeDisplays.Config.FILTER_BY_KELTNER;
import static com.kishlaly.ta.analyze.tasks.ThreeDisplays.Config.FILTER_BY_KELTNER_ENABLED;

public class Long_ScreenTwo_FilterLateEntry implements ScreenTwoBlock {
    @Override
    public BlockResultJava check(SymbolDataJava screen) {
        List<KeltnerJava> screen_2_Keltner = (List<KeltnerJava>) screen.indicators.get(IndicatorJava.KELTNER);
        if (FILTER_BY_KELTNER_ENABLED) {
            KeltnerJava lastKeltnerData = CollectionsTools.getFromEnd(screen_2_Keltner, 1);
            double lastQuoteClose = screen.getLastQuote().getClose();
            double middle = lastKeltnerData.getMiddle();
            double top = lastKeltnerData.getTop();
            double diff = top - middle;
            double ratio = diff / 100 * FILTER_BY_KELTNER;
            double maxAllowedCloseValue = middle + ratio;
            if (lastQuoteClose >= maxAllowedCloseValue) {
                Log.addDebugLine("The last quote closed above " + FILTER_BY_KELTNER + "% of the distance from the middle to the top of the channel");
                Log.recordCode(QUOTE_CLOSED_ABOVE_KELTNER_RULE_SCREEN_2, screen);
                return new BlockResultJava(screen.getLastQuote(), QUOTE_CLOSED_ABOVE_KELTNER_RULE_SCREEN_2);
            }
        }
        return new BlockResultJava(screen.getLastQuote(), OK);
    }
}
