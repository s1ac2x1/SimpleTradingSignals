package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.model.indicators.StochJava;
import com.kishlaly.ta.utils.LogJava;

import java.util.List;

import static com.kishlaly.ta.model.BlockResultCodeJava.OK;
import static com.kishlaly.ta.model.BlockResultCodeJava.STOCH_NOT_DESCENDING_SCREEN_2;

/**
 * stochastic should decrease from the overbought zone
 */
public class Short_ScreenTwo_Stoch_D_ThreeDescending implements ScreenTwoBlock {
    @Override
    public BlockResultJava check(SymbolDataJava screen) {
        List<StochJava> screen_2_Stochastic = (List<StochJava>) screen.indicators.get(IndicatorJava.STOCH);
        StochJava stoch3 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 3);
        StochJava stoch2 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 2);
        StochJava stoch1 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 1);
        // %D decreases (it is enough that the last one is lower than the last two)
        boolean ascendingStochastic = stoch1.getSlowD() < stoch2.getSlowD() && stoch1.getSlowD() < stoch3.getSlowD();
        if (!ascendingStochastic) {
            LogJava.recordCode(STOCH_NOT_DESCENDING_SCREEN_2, screen);
            LogJava.addDebugLine("Stochastic %D does not decrease on the second screen");
            return new BlockResultJava(screen.getLastQuote(), STOCH_NOT_DESCENDING_SCREEN_2);
        }
        return new BlockResultJava(screen.getLastQuote(), OK);
    }
}
