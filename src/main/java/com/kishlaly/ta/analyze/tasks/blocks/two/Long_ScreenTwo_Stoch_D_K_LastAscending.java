package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.model.indicators.Stoch;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.OK;
import static com.kishlaly.ta.analyze.BlockResultCode.STOCH_K_D_NOT_ASCENDING_SCREEN_2;

/**
 * %D and %K of the last stochastic should be higher than that of the penultimate stochastic
 */
public class Long_ScreenTwo_Stoch_D_K_LastAscending implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<Stoch> screen_2_Stochastic = (List<Stoch>) screen.indicators.get(Indicator.STOCH);
        Stoch screen_2_lastStoch = screen_2_Stochastic.get(screen_2_Stochastic.size() - 1);
        Stoch screen_2_preLastStoch = screen_2_Stochastic.get(screen_2_Stochastic.size() - 2);

        boolean ascending = screen_2_lastStoch.getSlowK() > screen_2_preLastStoch.getSlowK()
                && screen_2_lastStoch.getSlowD() > screen_2_preLastStoch.getSlowD();
        if (!ascending) {
            Log.recordCode(STOCH_K_D_NOT_ASCENDING_SCREEN_2, screen);
            Log.addDebugLine("Stochastic does not grow on the second screen");
            return new BlockResult(screen.getLastQuote(), STOCH_K_D_NOT_ASCENDING_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
