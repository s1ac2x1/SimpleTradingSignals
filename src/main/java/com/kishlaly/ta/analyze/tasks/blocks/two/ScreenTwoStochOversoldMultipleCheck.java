package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.model.indicators.Stoch;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.*;
import static com.kishlaly.ta.analyze.tasks.ThreeDisplays.Config.STOCH_OVERSOLD;
import static com.kishlaly.ta.analyze.tasks.ThreeDisplays.Config.STOCH_VALUES_TO_CHECK;
import static com.kishlaly.ta.utils.Quotes.resolveMinBarsCount;

/**
 * нужно проверять несколько стохастиков влево от последнего значения
 * например, 5 последних: если ли среди них значения ниже STOCH_OVERSOLD
 * но при условии, что медленная линия у правого края была выше
 * тогда STOCH_OVERSOLD можно держать поменьше, эдак 30
 */
public class ScreenTwoStochOversoldMultipleCheck implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<Stoch> screen_2_Stochastic = screen.indicators.get(Indicator.STOCH);
        Stoch stoch2 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 2);
        Stoch stoch1 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 1);

        boolean wasOversoldRecently = false;
        for (int i = resolveMinBarsCount(screen.timeframe) - STOCH_VALUES_TO_CHECK; i < resolveMinBarsCount(screen.timeframe); i++) {
            Stoch stoch = screen_2_Stochastic.get(i);
            if (stoch.getSlowD() <= STOCH_OVERSOLD || stoch.getSlowK() <= STOCH_OVERSOLD) {
                wasOversoldRecently = true;
            }
        }
        if (!wasOversoldRecently) {
            Log.recordCode(STOCH_WAS_NOT_OVERSOLD_RECENTLY, screen);
            Log.addDebugLine("Стохастик не был в перепроданности на последних " + STOCH_VALUES_TO_CHECK + " значениях");
            return new BlockResult(screen.getLastQuote(), STOCH_WAS_NOT_OVERSOLD_RECENTLY);
        }

        boolean lastStochIsBigger = stoch1.getSlowD() > stoch2.getSlowD();
        if (!lastStochIsBigger) {
            Log.recordCode(STOCH_NOT_ASCENDING_SCREEN_2, screen);
            Log.addDebugLine("Последние два значения стохастика не повышаются");
            return new BlockResult(screen.getLastQuote(), STOCH_NOT_ASCENDING_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
