package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.model.indicators.Stoch;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.OK;
import static com.kishlaly.ta.analyze.BlockResultCode.STOCH_D_K_NOT_ASCENDING_FROM_OVERSOLD_SCREEN_2;
import static com.kishlaly.ta.analyze.tasks.ThreeDisplays.Config.STOCH_OVERSOLD;

public class Long_ScreenTwo_Stoch_D_K_ThreeAscendingFromOversold implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<Stoch> screen_2_Stochastic = (List<Stoch>) screen.indicators.get(Indicator.STOCH);
        Stoch stoch3 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 3);
        Stoch stoch2 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 2);
        Stoch stoch1 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 1);

        // третья или вторая с конца %K ниже STOCH_OVERSOLD, и самая последняя выше первой
        boolean isOversoldK = (stoch3.getSlowK() <= STOCH_OVERSOLD || stoch2.getSlowK() <= STOCH_OVERSOLD)
                && (stoch1.getSlowK() > stoch3.getSlowK());

        // третья или вторая с конца %D ниже STOCH_OVERSOLD, и самая последняя выше обеих
        boolean isOversoldD = (stoch3.getSlowD() <= STOCH_OVERSOLD || stoch2.getSlowD() <= STOCH_OVERSOLD)
                && (stoch1.getSlowD() > stoch2.getSlowD())
                && (stoch1.getSlowD() > stoch3.getSlowD());

        if (!isOversoldK || !isOversoldD) {
            Log.recordCode(STOCH_D_K_NOT_ASCENDING_FROM_OVERSOLD_SCREEN_2, screen);
            Log.addDebugLine("Стохастик не поднимается из перепроданности " + STOCH_OVERSOLD + ". %D: " + isOversoldD + "; %K: " + isOversoldK);
            return new BlockResult(screen.getLastQuote(), STOCH_D_K_NOT_ASCENDING_FROM_OVERSOLD_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
