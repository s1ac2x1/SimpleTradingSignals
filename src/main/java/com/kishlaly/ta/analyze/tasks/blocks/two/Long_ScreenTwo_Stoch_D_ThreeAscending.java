package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.model.indicators.Stoch;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.OK;
import static com.kishlaly.ta.analyze.BlockResultCode.STOCH_K_NOT_ASCENDING_SCREEN_2;

/**
 * the last three %D stochastics are rising
 */
public class Long_ScreenTwo_Stoch_D_ThreeAscending implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<Stoch> screen_2_Stochastic = (List<Stoch>) screen.indicators.get(IndicatorJava.STOCH);
        Stoch stoch3 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 3);
        Stoch stoch2 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 2);
        Stoch stoch1 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 1);

        // it is enough that the last one is larger than the last two
        boolean ascendingStochastic = stoch1.getSlowD() > stoch2.getSlowD() && stoch1.getSlowD() > stoch3.getSlowD();
        if (!ascendingStochastic) {
            Log.recordCode(STOCH_K_NOT_ASCENDING_SCREEN_2, screen);
            Log.addDebugLine("Stochastic %D does not grow on the second screen");
            return new BlockResult(screen.getLastQuote(), STOCH_K_NOT_ASCENDING_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
