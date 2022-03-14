package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.EMA;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.LAST_BAR_ABOVE_SCREEN_2;
import static com.kishlaly.ta.analyze.BlockResultCode.OK;
import static com.kishlaly.ta.utils.Quotes.resolveMinBarsCount;

/**
 * нужно фильтровать ситуацию, когда третий и второй пересекают ЕМА13, а послдений целиком выше (момент входа в сделку упущен)
 * третий может открыться и закрыться выше, и это допустимо: https://drive.google.com/file/d/15XkXFKBQbTjeNjBn03NrF9JawCBFaO5t/view?usp=sharing
 */
public class Long_ScreenTwo_EMA_LastBarTooHigh implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        Quote quote3 = screen.quotes.get(resolveMinBarsCount(screen.timeframe) - 3);
        Quote quote2 = screen.quotes.get(resolveMinBarsCount(screen.timeframe) - 2);
        Quote quote1 = screen.quotes.get(resolveMinBarsCount(screen.timeframe) - 1);
        List<EMA> screen_2_EMA13 = screen.indicators.get(Indicator.EMA13);
        int screen_2_EMA13Count = screen_2_EMA13.size();

        boolean thirdCrossesEMA13 = quote3.getLow() < screen_2_EMA13.get(screen_2_EMA13Count - 3).getValue()
                && quote3.getHigh() > screen_2_EMA13.get(screen_2_EMA13Count - 3).getValue();
        boolean secondCrossesEMA13 = quote2.getLow() < screen_2_EMA13.get(screen_2_EMA13Count - 2).getValue()
                && quote2.getHigh() > screen_2_EMA13.get(screen_2_EMA13Count - 2).getValue();
        boolean lastAboveEMA13 = quote1.getLow() > screen_2_EMA13.get(screen_2_EMA13Count - 1).getValue()
                && quote1.getHigh() > screen_2_EMA13.get(screen_2_EMA13Count - 1).getValue();
        if (thirdCrossesEMA13 && secondCrossesEMA13 && lastAboveEMA13) {
            Log.recordCode(LAST_BAR_ABOVE_SCREEN_2, screen);
            Log.addDebugLine("Третий и второй пересекли ЕМА13, а последний полностью выше");
            return new BlockResult(screen.getLastQuote(), LAST_BAR_ABOVE_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
