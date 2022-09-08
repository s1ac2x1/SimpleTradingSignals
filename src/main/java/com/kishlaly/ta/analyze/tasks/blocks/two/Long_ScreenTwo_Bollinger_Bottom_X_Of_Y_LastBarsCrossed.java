package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.QuoteJava;
import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.model.indicators.BollingerJava;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.utils.LogJava;

import java.util.List;

import static com.kishlaly.ta.model.BlockResultCodeJava.LAST_QUOTE_NOT_CROSSED_BOLLINGER_BOTTOM_SCREEN_2;
import static com.kishlaly.ta.model.BlockResultCodeJava.OK;
import static com.kishlaly.ta.analyze.tasks.ThreeDisplays.Config.BOLLINGER_CROSSED_BOTTOM_BARS;
import static com.kishlaly.ta.analyze.tasks.ThreeDisplays.Config.BOLLINGER_TOTAL_BARS_CHECK;
import static com.kishlaly.ta.utils.QuotesJava.isQuoteCrossedBollingerBottom;

/**
 * X out of the last Y bars touched the bottom Bollinger band
 */
public class Long_ScreenTwo_Bollinger_Bottom_X_Of_Y_LastBarsCrossed implements ScreenTwoBlockJava {
    @Override
    public BlockResultJava check(SymbolDataJava screen) {
        if (BOLLINGER_TOTAL_BARS_CHECK < 0) {
            throw new RuntimeException("BOLLINGER_TOTAL_BARS_CHECK not defined");
        }
        if (BOLLINGER_CROSSED_BOTTOM_BARS < 0) {
            throw new RuntimeException("BOLLINGER_CROSSED_BOTTOM_BARS not defined");
        }

        List<BollingerJava> screen_2_Bollinger = (List<BollingerJava>) screen.indicators.get(IndicatorJava.BOLLINGER);

        int crossed = 0;
        for (int i = screen_2_Bollinger.size() - BOLLINGER_TOTAL_BARS_CHECK; i < screen_2_Bollinger.size(); i++) {
            QuoteJava quote = screen.quotes.get(i);
            BollingerJava bollinger = screen_2_Bollinger.get(i);
            if (isQuoteCrossedBollingerBottom(quote, bollinger)) {
                crossed++;
            }
        }

        if (crossed < BOLLINGER_CROSSED_BOTTOM_BARS) {
            LogJava.recordCode(LAST_QUOTE_NOT_CROSSED_BOLLINGER_BOTTOM_SCREEN_2, screen);
            LogJava.addDebugLine("X out of the last Y bars did not touch the bottom Bollinger band");
            return new BlockResultJava(screen.getLastQuote(), LAST_QUOTE_NOT_CROSSED_BOLLINGER_BOTTOM_SCREEN_2);
        }
        return new BlockResultJava(screen.getLastQuote(), OK);
    }
}
