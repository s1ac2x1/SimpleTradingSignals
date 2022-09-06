package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.model.indicators.StochJava;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.model.BlockResultCode.OK;
import static com.kishlaly.ta.model.BlockResultCode.STOCH_D_K_WAS_NOT_OVERSOLD_RECENTLY_SCREEN_2;
import static com.kishlaly.ta.analyze.tasks.ThreeDisplays.Config.STOCH_OVERSOLD;
import static com.kishlaly.ta.analyze.tasks.ThreeDisplays.Config.STOCH_VALUES_TO_CHECK;

/**
 * to check several stochastics to the left of the last value
 * e.g. the last STOCH_VALUES_TO_CHECK: if among them there are values below STOCH_OVERSOLD
 */
public class Long_ScreenTwo_Stoch_D_K_SomeWereOversold implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<StochJava> screen_2_Stochastic = (List<StochJava>) screen.indicators.get(IndicatorJava.STOCH);

        boolean wasOversoldRecently = false;
        for (int i = screen_2_Stochastic.size() - STOCH_VALUES_TO_CHECK; i < screen_2_Stochastic.size(); i++) {
            StochJava stoch = screen_2_Stochastic.get(i);
            if (stoch.getSlowD() <= STOCH_OVERSOLD || stoch.getSlowK() <= STOCH_OVERSOLD) {
                wasOversoldRecently = true;
            }
        }
        if (!wasOversoldRecently) {
            Log.recordCode(STOCH_D_K_WAS_NOT_OVERSOLD_RECENTLY_SCREEN_2, screen);
            Log.addDebugLine("Stochastic was not oversold on the last " + STOCH_VALUES_TO_CHECK + " values");
            return new BlockResult(screen.getLastQuote(), STOCH_D_K_WAS_NOT_OVERSOLD_RECENTLY_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
