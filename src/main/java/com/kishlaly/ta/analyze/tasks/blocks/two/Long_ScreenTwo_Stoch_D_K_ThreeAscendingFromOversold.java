package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.model.indicators.StochJava;
import com.kishlaly.ta.utils.LogJava;

import java.util.List;

import static com.kishlaly.ta.model.BlockResultCodeJava.OK;
import static com.kishlaly.ta.model.BlockResultCodeJava.STOCH_D_K_NOT_ASCENDING_FROM_OVERSOLD_SCREEN_2;
import static com.kishlaly.ta.analyze.tasks.ThreeDisplaysJava.Config.STOCH_OVERSOLD;

public class Long_ScreenTwo_Stoch_D_K_ThreeAscendingFromOversold implements ScreenTwoBlockJava {
    @Override
    public BlockResultJava check(SymbolDataJava screen) {
        List<StochJava> screen_2_Stochastic = (List<StochJava>) screen.indicators.get(IndicatorJava.STOCH);
        StochJava stoch3 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 3);
        StochJava stoch2 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 2);
        StochJava stoch1 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 1);

        // the third or second from the end of %K below STOCH_OVERSOLD, and the very last above the first
        boolean isOversoldK = (stoch3.getSlowK() <= STOCH_OVERSOLD || stoch2.getSlowK() <= STOCH_OVERSOLD)
                && (stoch1.getSlowK() > stoch3.getSlowK());

        // the third or second from the end of %D below STOCH_OVERSOLD, and the very last above both
        boolean isOversoldD = (stoch3.getSlowD() <= STOCH_OVERSOLD || stoch2.getSlowD() <= STOCH_OVERSOLD)
                && (stoch1.getSlowD() > stoch2.getSlowD())
                && (stoch1.getSlowD() > stoch3.getSlowD());

        if (!isOversoldK || !isOversoldD) {
            LogJava.recordCode(STOCH_D_K_NOT_ASCENDING_FROM_OVERSOLD_SCREEN_2, screen);
            LogJava.addDebugLine("Stochastic does not rise from oversold " + STOCH_OVERSOLD + ". %D: " + isOversoldD + "; %K: " + isOversoldK);
            return new BlockResultJava(screen.getLastQuote(), STOCH_D_K_NOT_ASCENDING_FROM_OVERSOLD_SCREEN_2);
        }
        return new BlockResultJava(screen.getLastQuote(), OK);
    }
}
