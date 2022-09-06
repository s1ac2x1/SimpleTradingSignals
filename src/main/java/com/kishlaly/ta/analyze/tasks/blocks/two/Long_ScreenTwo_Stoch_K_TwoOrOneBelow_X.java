package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.analyze.tasks.ThreeDisplays;
import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.model.indicators.StochJava;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.model.BlockResultCode.OK;
import static com.kishlaly.ta.model.BlockResultCode.STOCH_K_NOT_EXTRA_OVERSOLD_SCREEN_2;

/**
 * one of the two stochastic %K values is less than 20
 */
public class Long_ScreenTwo_Stoch_K_TwoOrOneBelow_X implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<StochJava> screen_2_Stochastic = (List<StochJava>) screen.indicators.get(IndicatorJava.STOCH);
        StochJava stoch2 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 2);
        StochJava stoch1 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 1);

        if (ThreeDisplays.Config.STOCH_CUSTOM < 0) {
            throw new RuntimeException("ThreeDisplays.Config.STOCH_CUSTOM not set");
        }

        boolean oneBelowExtraLow = stoch2.getSlowK() < ThreeDisplays.Config.STOCH_CUSTOM || stoch1.getSlowK() < ThreeDisplays.Config.STOCH_CUSTOM;

        if (!oneBelowExtraLow) {
            Log.recordCode(STOCH_K_NOT_EXTRA_OVERSOLD_SCREEN_2, screen);
            Log.addDebugLine("One of the last two stochastic %Ks is at least 20 on the second screen");
            return new BlockResult(screen.getLastQuote(), STOCH_K_NOT_EXTRA_OVERSOLD_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
