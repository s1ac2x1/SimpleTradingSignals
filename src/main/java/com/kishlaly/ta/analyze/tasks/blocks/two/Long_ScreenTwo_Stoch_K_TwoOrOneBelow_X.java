package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.analyze.tasks.ThreeDisplays;
import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.model.indicators.Stoch;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.OK;
import static com.kishlaly.ta.analyze.BlockResultCode.STOCH_K_NOT_EXTRA_OVERSOLD_SCREEN_2;

/**
 * одно из двух значений %K стохастика меньше 20
 */
public class Long_ScreenTwo_Stoch_K_TwoOrOneBelow_X implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<Stoch> screen_2_Stochastic = screen.indicators.get(Indicator.STOCH);
        Stoch stoch2 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 2);
        Stoch stoch1 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 1);

        if (ThreeDisplays.Config.STOCH_CUSTOM < 0) {
            throw new RuntimeException("ThreeDisplays.Config.STOCH_CUSTOM not set");
        }

        boolean oneBelowExtraLow = stoch2.getSlowK() < ThreeDisplays.Config.STOCH_CUSTOM || stoch1.getSlowK() < ThreeDisplays.Config.STOCH_CUSTOM;

        if (!oneBelowExtraLow) {
            Log.recordCode(STOCH_K_NOT_EXTRA_OVERSOLD_SCREEN_2, screen);
            Log.addDebugLine("Один из двух последних %K стохастика не ниже 20 на втором экране");
            return new BlockResult(screen.getLastQuote(), STOCH_K_NOT_EXTRA_OVERSOLD_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
