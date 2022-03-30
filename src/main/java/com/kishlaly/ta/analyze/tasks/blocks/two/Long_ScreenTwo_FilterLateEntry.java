package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.model.indicators.Keltner;
import com.kishlaly.ta.utils.CollectionsTools;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.OK;
import static com.kishlaly.ta.analyze.BlockResultCode.QUOTE_CLOSED_ABOVE_KELTNER_RULE_SCREEN_2;
import static com.kishlaly.ta.analyze.tasks.ThreeDisplays.Config.FILTER_BY_KELTNER;
import static com.kishlaly.ta.analyze.tasks.ThreeDisplays.Config.FILTER_BY_KELTNER_ENABLED;

public class Long_ScreenTwo_FilterLateEntry implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<Keltner> screen_2_Keltner = screen.indicators.get(Indicator.KELTNER);
        if (FILTER_BY_KELTNER_ENABLED) {
            Keltner lastKeltnerData = CollectionsTools.getFromEnd(screen_2_Keltner, 1);
            double lastQuoteClose = screen.getLastQuote().getClose();
            double middle = lastKeltnerData.getMiddle();
            double top = lastKeltnerData.getTop();
            double diff = top - middle;
            double ratio = diff / 100 * FILTER_BY_KELTNER;
            double maxAllowedCloseValue = middle + ratio;
            if (lastQuoteClose >= maxAllowedCloseValue) {
                Log.addDebugLine("Последняя котировка закрылась выше " + FILTER_BY_KELTNER + "% расстояния от середины до вершины канала");
                Log.recordCode(QUOTE_CLOSED_ABOVE_KELTNER_RULE_SCREEN_2, screen);
                return new BlockResult(screen.getLastQuote(), QUOTE_CLOSED_ABOVE_KELTNER_RULE_SCREEN_2);
            }
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
