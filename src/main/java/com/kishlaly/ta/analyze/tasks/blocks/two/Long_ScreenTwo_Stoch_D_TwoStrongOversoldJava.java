package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.model.indicators.StochJava;
import com.kishlaly.ta.utils.LogJava;

import java.util.List;

import static com.kishlaly.ta.model.BlockResultCodeJava.OK;
import static com.kishlaly.ta.model.BlockResultCodeJava.STOCH_D_WAS_NOT_STRONG_OVERSOLD_RECENTLY_SCREEN_2;

/**
 * oversold below 20 at TWO values of the slow stochastic line and it goes up
 */
public class Long_ScreenTwo_Stoch_D_TwoStrongOversoldJava implements ScreenTwoBlockJava {
    @Override
    public BlockResultJava check(SymbolDataJava screen) {
        List<StochJava> screen_2_Stochastic = (List<StochJava>) screen.indicators.get(IndicatorJava.STOCH);
        StochJava stoch2 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 2);
        StochJava stoch1 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 1);

        boolean oversold = stoch2.getSlowD() < 20 && stoch1.getSlowD() < 20;
        if (!oversold) {
            LogJava.recordCode(STOCH_D_WAS_NOT_STRONG_OVERSOLD_RECENTLY_SCREEN_2, screen);
            LogJava.addDebugLine("The last two stochastic %D values are at least 20");
            return new BlockResultJava(screen.getLastQuote(), STOCH_D_WAS_NOT_STRONG_OVERSOLD_RECENTLY_SCREEN_2);
        }
        return new BlockResultJava(screen.getLastQuote(), OK);
    }
}
