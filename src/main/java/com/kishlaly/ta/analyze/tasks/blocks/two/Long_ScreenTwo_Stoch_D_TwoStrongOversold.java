package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.model.indicators.Stoch;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.OK;
import static com.kishlaly.ta.analyze.BlockResultCode.STOCH_D_WAS_NOT_STRONG_OVERSOLD_RECENTLY_SCREEN_2;

/**
 * oversold below 20 at TWO values of the slow stochastic line and it goes up
 */
public class Long_ScreenTwo_Stoch_D_TwoStrongOversold implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<Stoch> screen_2_Stochastic = (List<Stoch>) screen.indicators.get(IndicatorJava.STOCH);
        Stoch stoch2 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 2);
        Stoch stoch1 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 1);

        boolean oversold = stoch2.getSlowD() < 20 && stoch1.getSlowD() < 20;
        if (!oversold) {
            Log.recordCode(STOCH_D_WAS_NOT_STRONG_OVERSOLD_RECENTLY_SCREEN_2, screen);
            Log.addDebugLine("The last two stochastic %D values are at least 20");
            return new BlockResult(screen.getLastQuote(), STOCH_D_WAS_NOT_STRONG_OVERSOLD_RECENTLY_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
