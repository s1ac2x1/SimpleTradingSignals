package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.model.indicators.Stoch;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.OK;
import static com.kishlaly.ta.analyze.BlockResultCode.STOCH_D_NOT_ASCENDING_SCREEN_2;

/**
 * последняя %D стохастика выше
 */
public class Long_ScreenTwo_Stoch_D_TwoAscending implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<Stoch> screen_2_Stochastic = screen.indicators.get(Indicator.STOCH);
        Stoch stoch2 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 2);
        Stoch stoch1 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 1);

        boolean ascendingStochastic = stoch2.getSlowD() < stoch1.getSlowD();
        if (!ascendingStochastic) {
            Log.recordCode(STOCH_D_NOT_ASCENDING_SCREEN_2, screen);
            Log.addDebugLine("Стохастик %D не растет на втором экране");
            return new BlockResult(screen.getLastQuote(), STOCH_D_NOT_ASCENDING_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
