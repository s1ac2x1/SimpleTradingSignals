package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.model.indicators.Stoch;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.*;

/**
 * перепроданность ниже 20 у трех значений медленной линии стохастика и она повышается
 */
public class Long_ScreenTwo_Stoch_D_K_ThreeAscendingFromStrongOversold implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<Stoch> screen_2_Stochastic = screen.indicators.get(Indicator.STOCH);
        Stoch stoch3 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 3);
        Stoch stoch2 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 2);
        Stoch stoch1 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 1);

        boolean oversold = stoch3.getSlowD() < 20 && stoch2.getSlowD() < 20 && stoch1.getSlowD() < 20;
        if (!oversold) {
            Log.recordCode(STOCH_WAS_NOT_OVERSOLD_RECENTLY_SCREEN_2, screen);
            Log.addDebugLine("Три последних значения %D стохастика не ниже 20");
            return new BlockResult(screen.getLastQuote(), STOCH_WAS_NOT_OVERSOLD_RECENTLY_SCREEN_2);
        }
        boolean stochAscending = stoch3.getSlowD() < stoch2.getSlowD() && stoch2.getSlowD() < stoch1.getSlowD();
        if (!stochAscending) {
            Log.recordCode(STOCH_NOT_ASCENDING_SCREEN_2, screen);
            Log.addDebugLine("Три последних значения %D стохастика не повышаются");
            return new BlockResult(screen.getLastQuote(), STOCH_NOT_ASCENDING_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
