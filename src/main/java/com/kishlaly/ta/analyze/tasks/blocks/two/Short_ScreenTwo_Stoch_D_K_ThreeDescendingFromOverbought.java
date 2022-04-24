package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.model.indicators.Stoch;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.OK;
import static com.kishlaly.ta.analyze.BlockResultCode.STOCH_NOT_DESCENDING_FROM_OVERBOUGHT_SCREEN_2;
import static com.kishlaly.ta.analyze.tasks.ThreeDisplays.Config.STOCH_OVERBOUGHT;

/**
 * проверка перекупленности
 */
public class Short_ScreenTwo_Stoch_D_K_ThreeDescendingFromOverbought implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<Stoch> screen_2_Stochastic = (List<Stoch>) screen.indicators.get(Indicator.STOCH);
        Stoch stoch3 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 3);
        Stoch stoch2 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 2);
        Stoch stoch1 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 1);

        // третья или вторая с конца %K выше STOCH_OVERSOLD, и самая последняя ниже первой
        boolean isOverboughtK = (stoch3.getSlowK() >= STOCH_OVERBOUGHT || stoch2.getSlowK() >= STOCH_OVERBOUGHT)
                && (stoch1.getSlowK() < stoch3.getSlowK());
        // третья или вторая с конца %D выше STOCH_OVERSOLD, и самая последняя ниже обеих
        boolean isOverboughtD = (stoch3.getSlowD() >= STOCH_OVERBOUGHT || stoch2.getSlowD() >= STOCH_OVERBOUGHT)
                && (stoch1.getSlowD() < stoch2.getSlowD())
                && (stoch1.getSlowD() < stoch3.getSlowD());

        if (!isOverboughtK || !isOverboughtD) {
            Log.recordCode(STOCH_NOT_DESCENDING_FROM_OVERBOUGHT_SCREEN_2, screen);
            Log.addDebugLine("Стохастик не снижается из перекупленности " + STOCH_OVERBOUGHT + ". %D: " + isOverboughtD + "; %K: " + isOverboughtK);
            return new BlockResult(screen.getLastQuote(), STOCH_NOT_DESCENDING_FROM_OVERBOUGHT_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
