package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.model.indicators.StochJava;
import com.kishlaly.ta.utils.LogJava;

import java.util.List;

import static com.kishlaly.ta.model.BlockResultCodeJava.OK;
import static com.kishlaly.ta.model.BlockResultCodeJava.STOCH_D_NOT_ASCENDING_SCREEN_2;

/**
 * last %D stochastics above
 */
public class Long_ScreenTwo_Stoch_D_TwoAscending implements ScreenTwoBlock {
    @Override
    public BlockResultJava check(SymbolDataJava screen) {
        List<StochJava> screen_2_Stochastic = (List<StochJava>) screen.indicators.get(IndicatorJava.STOCH);
        StochJava stoch2 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 2);
        StochJava stoch1 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 1);

        boolean ascendingStochastic = stoch2.getSlowD() < stoch1.getSlowD();
        if (!ascendingStochastic) {
            LogJava.recordCode(STOCH_D_NOT_ASCENDING_SCREEN_2, screen);
            LogJava.addDebugLine("Stochastic %D does not grow on the second screen");
            return new BlockResultJava(screen.getLastQuote(), STOCH_D_NOT_ASCENDING_SCREEN_2);
        }
        return new BlockResultJava(screen.getLastQuote(), OK);
    }
}
