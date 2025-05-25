package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.ADX;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.ADAPTIVE_ADX_BELOW_THRESHOLD_SCREEN_2;
import static com.kishlaly.ta.analyze.BlockResultCode.OK;

/**
 * текущий ADX с средним ADX за последние N баров, умноженный на некоторый коэффициент k (например, 1.1–1.3)
 */
public class Long_ScreenTwo_ADX_AdaptiveAbove implements ScreenTwoBlock {

    private final int period;     // 14
    private final int lookback;   // скользящее окно для среднего ADX, например 50
    private final double mult;    // коэффициент, например 1.2

    public Long_ScreenTwo_ADX_AdaptiveAbove(int period, int lookback, double mult) {
        this.period = period;
        this.lookback = lookback;
        this.mult = mult;
    }

    @Override
    public BlockResult check(SymbolData screen) {
        List<ADX> screen_2_adx = (List<ADX>) screen.indicators.get(Indicator.ADX);

        int lastIdx = screen_2_adx.size() - 1;
        // Текущий ADX
        double currentAdx = screen_2_adx.get(lastIdx).getValue();

        // Вычисляем скользящее среднее ADX за lookback баров (не включая последний)
        double sum = 0;
        int count = 0;
        for (int i = Math.max(0, lastIdx - lookback); i < lastIdx; i++) {
            sum += screen_2_adx.get(i).getValue();
            count++;
        }
        double avgAdx = count > 0 ? sum / count : currentAdx;

        // Адаптивный порог
        double threshold = avgAdx * mult;

        if (currentAdx <= threshold) {
            Log.recordCode(ADAPTIVE_ADX_BELOW_THRESHOLD_SCREEN_2, screen);
            Log.addDebugLine("Adaptive ADX below threshold on the second screen");
            return new BlockResult(screen.getLastQuote(), ADAPTIVE_ADX_BELOW_THRESHOLD_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
