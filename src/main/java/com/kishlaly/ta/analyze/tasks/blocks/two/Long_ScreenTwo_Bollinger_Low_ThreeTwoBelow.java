package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.Bollinger;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.utils.CollectionsTools;
import com.kishlaly.ta.utils.Log;
import com.kishlaly.ta.utils.Quotes;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.OK;
import static com.kishlaly.ta.analyze.BlockResultCode.QUOTE_3_AND_QUOTE_2_NOT_BELOW_BOLLINGER_BOTTOM_SCREEN_2;

/**
 * третья и вторая с конца котировки ниже нижней ленты Боллинжера
 */
public class Long_ScreenTwo_Bollinger_Low_ThreeTwoBelow implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<Quote> quotes = screen.quotes;
        List<Bollinger> screen_2_Bollinger = (List<Bollinger>) screen.indicators.get(Indicator.BOLLINGER);
        Quote quote_3 = CollectionsTools.getFromEnd(quotes, 3);
        Quote quote_2 = CollectionsTools.getFromEnd(quotes, 2);
        Bollinger bollinger_3 = CollectionsTools.getFromEnd(screen_2_Bollinger, 3);
        Bollinger bollinger_2 = CollectionsTools.getFromEnd(screen_2_Bollinger, 2);

        boolean below = Quotes.isQuoteBelowBollingerBottom(quote_3, bollinger_3) && Quotes.isQuoteBelowBollingerBottom(quote_2, bollinger_2);

        if (!below) {
            Log.recordCode(QUOTE_3_AND_QUOTE_2_NOT_BELOW_BOLLINGER_BOTTOM_SCREEN_2, screen);
            Log.addDebugLine("Третья и вторая с конца котировки не ниже нижней ленты Боллинжера на втором экране");
            return new BlockResult(screen.getLastQuote(), QUOTE_3_AND_QUOTE_2_NOT_BELOW_BOLLINGER_BOTTOM_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
