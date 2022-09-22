package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.analyze.tasks.ThreeDisplaysJava;
import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.model.indicators.StochJava;
import com.kishlaly.ta.utils.LogJava;

import java.util.List;

import static com.kishlaly.ta.model.BlockResultCodeJava.OK;
import static com.kishlaly.ta.model.BlockResultCodeJava.STOCH_K_NOT_EXTRA_OVERSOLD_SCREEN_2;

/**
 * one of the two stochastic %K values is less than 20
 */
public class Long_ScreenTwo_Stoch_K_TwoOrOneBelow_XJava implements ScreenTwoBlockJava {
    @Override
    public BlockResultJava check(SymbolDataJava screen) {
        List<StochJava> screen_2_Stochastic = (List<StochJava>) screen.indicators.get(IndicatorJava.STOCH);
        StochJava stoch2 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 2);
        StochJava stoch1 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 1);

        if (ThreeDisplaysJava.Config.STOCH_CUSTOM < 0) {
            throw new RuntimeException("ThreeDisplays.Config.STOCH_CUSTOM not set");
        }

        boolean oneBelowExtraLow = stoch2.getSlowK() < ThreeDisplaysJava.Config.STOCH_CUSTOM || stoch1.getSlowK() < ThreeDisplaysJava.Config.STOCH_CUSTOM;

        if (!oneBelowExtraLow) {
            LogJava.recordCode(STOCH_K_NOT_EXTRA_OVERSOLD_SCREEN_2, screen);
            LogJava.addDebugLine("One of the last two stochastic %Ks is at least 20 on the second screen");
            return new BlockResultJava(screen.getLastQuote(), STOCH_K_NOT_EXTRA_OVERSOLD_SCREEN_2);
        }
        return new BlockResultJava(screen.getLastQuote(), OK);
    }
}
