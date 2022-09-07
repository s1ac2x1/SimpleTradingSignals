package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.QuoteJava;
import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.model.indicators.KeltnerJava;
import com.kishlaly.ta.utils.LogJava;
import com.kishlaly.ta.utils.Quotes;

import java.util.List;

import static com.kishlaly.ta.model.BlockResultCodeJava.OK;
import static com.kishlaly.ta.model.BlockResultCodeJava.X_FROM_END_QUOTE_DIDNT_CROSSED_KELTNER_BOTTOM_SCREEN_2;
import static com.kishlaly.ta.analyze.tasks.ThreeDisplays.Config.QUOTE_FROM_END_TO_USE;

public class Long_ScreenTwo_Keltner_Bottom_X_FromEndCrossed implements ScreenTwoBlock {
    @Override
    public BlockResultJava check(SymbolDataJava screen) {
        if (QUOTE_FROM_END_TO_USE < 0) {
            throw new RuntimeException("QUOTE_FROM_END_TO_USE not defined");
        }
        List<KeltnerJava> screen_2_Keltner = (List<KeltnerJava>) screen.indicators.get(IndicatorJava.KELTNER);
        KeltnerJava keltner = screen_2_Keltner.get(screen_2_Keltner.size() - 1 - QUOTE_FROM_END_TO_USE);
        QuoteJava quote = screen.quotes.get(screen.quotes.size() - 1 - QUOTE_FROM_END_TO_USE);

        if (!Quotes.isQuoteCrossedKeltnerBottom(quote, keltner)) {
            LogJava.addDebugLine(QUOTE_FROM_END_TO_USE + " quote from the end the has not crossed the lower boundary of the Keltner channel");
            LogJava.recordCode(X_FROM_END_QUOTE_DIDNT_CROSSED_KELTNER_BOTTOM_SCREEN_2, screen);
            return new BlockResultJava(screen.getLastQuote(), X_FROM_END_QUOTE_DIDNT_CROSSED_KELTNER_BOTTOM_SCREEN_2);
        }
        return new BlockResultJava(screen.getLastQuote(), OK);
    }
}
