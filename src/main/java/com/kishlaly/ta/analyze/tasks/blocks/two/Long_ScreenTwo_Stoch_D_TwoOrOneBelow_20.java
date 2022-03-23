package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.model.indicators.Stoch;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.OK;
import static com.kishlaly.ta.analyze.BlockResultCode.STOCH_D_NOT_EXTRA_OVERSOLD_SCREEN_2;

/**
 * одно из двух значений %D стохастика меньше 20
 */
public class Long_ScreenTwo_Stoch_D_TwoOrOneBelow_20 implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<Stoch> screen_2_Stochastic = screen.indicators.get(Indicator.STOCH);
        Stoch stoch2 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 2);
        Stoch stoch1 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 1);

        boolean oneBelowExtraLow = stoch2.getSlowD() < 20 || stoch1.getSlowD() < 20;

        if (!oneBelowExtraLow) {
            Log.recordCode(STOCH_D_NOT_EXTRA_OVERSOLD_SCREEN_2, screen);
            Log.addDebugLine("Один из двух последних %D стохастика не ниже 20 на втором экране");
            return new BlockResult(screen.getLastQuote(), STOCH_D_NOT_EXTRA_OVERSOLD_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
