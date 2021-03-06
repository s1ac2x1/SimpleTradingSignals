package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.Bollinger;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.LAST_QUOTE_NOT_CROSSED_BOLLINGER_BOTTOM_SCREEN_2;
import static com.kishlaly.ta.analyze.BlockResultCode.OK;
import static com.kishlaly.ta.utils.Quotes.isQuoteCrossedBollingerBottom;

/**
 * the last bar crossed the lower Bollinger band
 */
public class Long_ScreenTwo_Bollinger_Bottom_LastBarCrossed implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<Bollinger> screen_2_Bollinger = (List<Bollinger>) screen.indicators.get(Indicator.BOLLINGER);
        if (isQuoteCrossedBollingerBottom(screen.getLastQuote(), screen_2_Bollinger.get(screen_2_Bollinger.size() - 1))) {
            Log.recordCode(LAST_QUOTE_NOT_CROSSED_BOLLINGER_BOTTOM_SCREEN_2, screen);
            Log.addDebugLine("The last bar didn't cross the lower Bollinger band");
            return new BlockResult(screen.getLastQuote(), LAST_QUOTE_NOT_CROSSED_BOLLINGER_BOTTOM_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
