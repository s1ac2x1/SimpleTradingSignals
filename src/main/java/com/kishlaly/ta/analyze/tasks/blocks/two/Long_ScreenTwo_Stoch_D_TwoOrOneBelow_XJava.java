package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.analyze.tasks.ThreeDisplaysJava;
import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.model.indicators.StochJava;
import com.kishlaly.ta.utils.LogJava;

import java.util.List;

import static com.kishlaly.ta.model.BlockResultCodeJava.OK;
import static com.kishlaly.ta.model.BlockResultCodeJava.STOCH_D_NOT_EXTRA_OVERSOLD_SCREEN_2;

/**
 * one of the two stochastic %D values is less than ThreeDisplays.Config.STOCH_CUSTOM
 */
public class Long_ScreenTwo_Stoch_D_TwoOrOneBelow_XJava implements ScreenTwoBlockJava {
    @Override
    public BlockResultJava check(SymbolDataJava screen) {
        List<StochJava> screen_2_Stochastic = (List<StochJava>) screen.indicators.get(IndicatorJava.STOCH);
        StochJava stoch2 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 2);
        StochJava stoch1 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 1);

        if (ThreeDisplaysJava.Config.STOCH_CUSTOM < 0) {
            throw new RuntimeException("ThreeDisplays.Config.STOCH_CUSTOM not set");
        }

        boolean oneBelowExtraLow = stoch2.getSlowD() < ThreeDisplaysJava.Config.STOCH_CUSTOM || stoch1.getSlowD() < ThreeDisplaysJava.Config.STOCH_CUSTOM;

        if (!oneBelowExtraLow) {
            LogJava.recordCode(STOCH_D_NOT_EXTRA_OVERSOLD_SCREEN_2, screen);
            LogJava.addDebugLine("One of the last two %D stochastics is not lower than " + ThreeDisplaysJava.Config.STOCH_CUSTOM + " on the second screen");
            return new BlockResultJava(screen.getLastQuote(), STOCH_D_NOT_EXTRA_OVERSOLD_SCREEN_2);
        }
        return new BlockResultJava(screen.getLastQuote(), OK);
    }
}
