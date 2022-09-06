package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.QuoteJava;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.model.indicators.Keltner;
import com.kishlaly.ta.utils.Log;
import com.kishlaly.ta.utils.Quotes;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.OK;
import static com.kishlaly.ta.analyze.BlockResultCode.X_FROM_END_QUOTE_DIDNT_CROSSED_KELTNER_BOTTOM_SCREEN_2;
import static com.kishlaly.ta.analyze.tasks.ThreeDisplays.Config.QUOTE_FROM_END_TO_USE;

public class Long_ScreenTwo_Keltner_Bottom_X_FromEndCrossed implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        if (QUOTE_FROM_END_TO_USE < 0) {
            throw new RuntimeException("QUOTE_FROM_END_TO_USE not defined");
        }
        List<Keltner> screen_2_Keltner = (List<Keltner>) screen.indicators.get(IndicatorJava.KELTNER);
        Keltner keltner = screen_2_Keltner.get(screen_2_Keltner.size() - 1 - QUOTE_FROM_END_TO_USE);
        QuoteJava quote = screen.quotes.get(screen.quotes.size() - 1 - QUOTE_FROM_END_TO_USE);

        if (!Quotes.isQuoteCrossedKeltnerBottom(quote, keltner)) {
            Log.addDebugLine(QUOTE_FROM_END_TO_USE + " quote from the end the has not crossed the lower boundary of the Keltner channel");
            Log.recordCode(X_FROM_END_QUOTE_DIDNT_CROSSED_KELTNER_BOTTOM_SCREEN_2, screen);
            return new BlockResult(screen.getLastQuote(), X_FROM_END_QUOTE_DIDNT_CROSSED_KELTNER_BOTTOM_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
