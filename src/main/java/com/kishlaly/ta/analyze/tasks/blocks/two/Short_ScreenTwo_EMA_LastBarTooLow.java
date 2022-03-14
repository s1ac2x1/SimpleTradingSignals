package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.EMA;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.LAST_BAR_BELOW_SCREEN_2;
import static com.kishlaly.ta.analyze.BlockResultCode.OK;
import static com.kishlaly.ta.utils.Quotes.resolveMinBarsCount;

/**
 * нужно фильтровать ситуацию, когда третий и второй пересекают ЕМА13, а последний целиком ниже (то есть уже момент потерян)
 */
public class Short_ScreenTwo_EMA_LastBarTooLow implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<EMA> screen_2_EMA13 = screen.indicators.get(Indicator.EMA13);
        int minBarsCount = resolveMinBarsCount(screen.timeframe);
        Quote quote3 = screen.quotes.get(minBarsCount - 3);
        Quote quote2 = screen.quotes.get(minBarsCount - 2);
        Quote quote1 = screen.quotes.get(minBarsCount - 1);

        boolean thirdCrossesEMA13 = quote3.getLow() < screen_2_EMA13.get(minBarsCount - 3).getValue()
                && quote3.getHigh() > screen_2_EMA13.get(minBarsCount - 3).getValue();
        boolean secondCrossesEMA13 = quote2.getLow() < screen_2_EMA13.get(minBarsCount - 2).getValue()
                && quote2.getHigh() > screen_2_EMA13.get(minBarsCount - 2).getValue();
        boolean lastBelowEMA13 = quote1.getLow() < screen_2_EMA13.get(minBarsCount - 1).getValue()
                && quote1.getHigh() < screen_2_EMA13.get(minBarsCount - 1).getValue();
        if (thirdCrossesEMA13 && secondCrossesEMA13 && lastBelowEMA13) {
            Log.recordCode(LAST_BAR_BELOW_SCREEN_2, screen);
            Log.addDebugLine("Третий и второй пересекли ЕМА13, а последний полностью ниже");
            return new BlockResult(screen.getLastQuote(), LAST_BAR_BELOW_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
