package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.model.indicators.StochJava;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.model.BlockResultCodeJava.OK;
import static com.kishlaly.ta.model.BlockResultCodeJava.STOCH_NOT_DESCENDING_FROM_OVERBOUGHT_SCREEN_2;
import static com.kishlaly.ta.analyze.tasks.ThreeDisplays.Config.STOCH_OVERBOUGHT;

/**
 * overbought check
 */
public class Short_ScreenTwo_Stoch_D_K_ThreeDescendingFromOverbought implements ScreenTwoBlock {
    @Override
    public BlockResultJava check(SymbolData screen) {
        List<StochJava> screen_2_Stochastic = (List<StochJava>) screen.indicators.get(IndicatorJava.STOCH);
        StochJava stoch3 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 3);
        StochJava stoch2 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 2);
        StochJava stoch1 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 1);

        // the third or second from the end of %K above STOCH_OVERSOLD, and the very last below the first
        boolean isOverboughtK = (stoch3.getSlowK() >= STOCH_OVERBOUGHT || stoch2.getSlowK() >= STOCH_OVERBOUGHT)
                && (stoch1.getSlowK() < stoch3.getSlowK());
        // the third or second from the end of %D above STOCH_OVERSOLD, and the very last below both
        boolean isOverboughtD = (stoch3.getSlowD() >= STOCH_OVERBOUGHT || stoch2.getSlowD() >= STOCH_OVERBOUGHT)
                && (stoch1.getSlowD() < stoch2.getSlowD())
                && (stoch1.getSlowD() < stoch3.getSlowD());

        if (!isOverboughtK || !isOverboughtD) {
            Log.recordCode(STOCH_NOT_DESCENDING_FROM_OVERBOUGHT_SCREEN_2, screen);
            Log.addDebugLine("Stochastic is not going down from overbought " + STOCH_OVERBOUGHT + ". %D: " + isOverboughtD + "; %K: " + isOverboughtK);
            return new BlockResultJava(screen.getLastQuote(), STOCH_NOT_DESCENDING_FROM_OVERBOUGHT_SCREEN_2);
        }
        return new BlockResultJava(screen.getLastQuote(), OK);
    }
}
