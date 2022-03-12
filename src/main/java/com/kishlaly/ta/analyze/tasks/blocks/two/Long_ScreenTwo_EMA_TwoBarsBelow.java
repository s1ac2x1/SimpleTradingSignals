package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.EMA;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.OK;
import static com.kishlaly.ta.analyze.BlockResultCode.QUOTES_NOT_BELOW_EMA_SCREEN_2;
import static com.kishlaly.ta.utils.Quotes.resolveMinBarsCount;

/**
 * последние два бара полностью ниже ЕМА13
 */
public class Long_ScreenTwo_EMA_TwoBarsBelow implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<EMA> screen_2_EMA13 = screen.indicators.get(Indicator.EMA13);
        EMA ema13_2 = screen_2_EMA13.get(resolveMinBarsCount(screen.timeframe) - 2);
        EMA ema13_1 = screen_2_EMA13.get(resolveMinBarsCount(screen.timeframe) - 1);
        List<Quote> screen_2_Quotes = screen.quotes;
        Quote quote2 = screen_2_Quotes.get(resolveMinBarsCount(screen.timeframe) - 2);
        Quote quote1 = screen_2_Quotes.get(resolveMinBarsCount(screen.timeframe) - 1);

        boolean lastQuotesBelowEMA = quote2.getHigh() < ema13_2.getValue() && quote1.getHigh() < ema13_1.getValue();
        if (!lastQuotesBelowEMA) {
            Log.recordCode(QUOTES_NOT_BELOW_EMA_SCREEN_2, screen);
            Log.addDebugLine("Последние две котировки не ниже EMA13");
            return new BlockResult(screen.getLastQuote(), QUOTES_NOT_BELOW_EMA_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
