package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.model.indicators.StochJava;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.model.BlockResultCodeJava.OK;
import static com.kishlaly.ta.model.BlockResultCodeJava.STOCH_K_NOT_ASCENDING_SCREEN_2;

/**
 * the slow line at the right edge is above
 */
public class Long_ScreenTwo_Stoch_D_LastAscending implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<StochJava> screen_2_Stochastic = (List<StochJava>) screen.indicators.get(IndicatorJava.STOCH);
        StochJava stoch2 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 2);
        StochJava stoch1 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 1);

        boolean lastStochIsBigger = stoch1.getSlowD() > stoch2.getSlowD();
        if (!lastStochIsBigger) {
            Log.recordCode(STOCH_K_NOT_ASCENDING_SCREEN_2, screen);
            Log.addDebugLine("The last two %D stochastics do not go up");
            return new BlockResult(screen.getLastQuote(), STOCH_K_NOT_ASCENDING_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
