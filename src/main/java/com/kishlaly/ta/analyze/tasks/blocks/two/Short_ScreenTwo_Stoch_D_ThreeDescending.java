package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.model.indicators.Stoch;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.OK;
import static com.kishlaly.ta.analyze.BlockResultCode.STOCH_NOT_DESCENDING_SCREEN_2;

/**
 * стохастик должен снижаться из зоны перекупленности
 */
public class Short_ScreenTwo_Stoch_D_ThreeDescending implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<Stoch> screen_2_Stochastic = screen.indicators.get(Indicator.STOCH);
        Stoch stoch3 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 3);
        Stoch stoch2 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 2);
        Stoch stoch1 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 1);
        // %D снижается (достаточно, чтобы последний был ниже прошлых двух)
        boolean ascendingStochastic = stoch1.getSlowD() < stoch2.getSlowD() && stoch1.getSlowD() < stoch3.getSlowD();
        if (!ascendingStochastic) {
            Log.recordCode(STOCH_NOT_DESCENDING_SCREEN_2, screen);
            Log.addDebugLine("Стохастик %D не снижается на втором экране");
            return new BlockResult(screen.getLastQuote(), STOCH_NOT_DESCENDING_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}