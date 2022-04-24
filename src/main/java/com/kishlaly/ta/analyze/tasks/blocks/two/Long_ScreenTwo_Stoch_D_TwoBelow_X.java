package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.analyze.tasks.ThreeDisplays;
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.model.indicators.Stoch;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.OK;
import static com.kishlaly.ta.analyze.BlockResultCode.STOCH_D_NOT_EXTRA_OVERSOLD_SCREEN_2;

/**
 * последние две %D стохастика ниже ThreeDisplays.Config.STOCH_CUSTOM
 */
public class Long_ScreenTwo_Stoch_D_TwoBelow_X implements TaskBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<Stoch> screen_2_Stochastic = (List<Stoch>) screen.indicators.get(Indicator.STOCH);
        Stoch stoch2 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 2);
        Stoch stoch1 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 1);

        if (ThreeDisplays.Config.STOCH_CUSTOM < 0) {
            throw new RuntimeException("ThreeDisplays.Config.STOCH_CUSTOM not set");
        }

        boolean bothBelowExtraLow = stoch2.getSlowD() < ThreeDisplays.Config.STOCH_CUSTOM && stoch1.getSlowD() < ThreeDisplays.Config.STOCH_CUSTOM;

        if (!bothBelowExtraLow) {
            Log.recordCode(STOCH_D_NOT_EXTRA_OVERSOLD_SCREEN_2, screen);
            Log.addDebugLine("Оба последних %D стохастика не ниже 20 на втором экране");
            return new BlockResult(screen.getLastQuote(), STOCH_D_NOT_EXTRA_OVERSOLD_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
