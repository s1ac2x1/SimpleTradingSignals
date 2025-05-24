package com.kishlaly.ta.analyze.testing.tp;

import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.Keltner;
import com.kishlaly.ta.utils.IndicatorUtils;

/**
 * TP на верхнем Keltner'е, но коэффициент автоматически
 * сужается / расширяется в зависимости от текущей ATR-волатильности.
 * <p>
 * Базовый % (например 80) передаётся в config, как и раньше.
 */
public class TakeProfitAdaptiveKeltnerTop extends TakeProfitStrategy {

    private static final int ATR_SHORT = 20;   // как в классическом Keltner
    private static final int ATR_LONG = 100;  // «нормальная» волатильность
    private static final double FLOOR = 0.7;  // нижний предел volRatio
    private static final double CEIL = 1.3;  // верхний предел volRatio

    public TakeProfitAdaptiveKeltnerTop(Object config) {
        super(config, false);
    }

    @Override
    public double calcualte(SymbolData data, int signalIndex) {
        // --- 1. Базовые компоненты канала
        Keltner k = IndicatorUtils.buildKeltnerChannels(data.symbol, data.quotes)
                .get(signalIndex);
        double middle = k.getMiddle();

        // --- 2. ATR (короткий и длинный) на той же свече
        double atr20 = IndicatorUtils.buildATR(data.symbol, data.quotes, ATR_SHORT)
                .get(signalIndex).getValue();
        double atr100 = IndicatorUtils.buildATR(data.symbol, data.quotes, ATR_LONG)
                .get(signalIndex).getValue();

        // защита от NaN на первых барах
        if (Double.isNaN(atr20) || Double.isNaN(atr100) || atr100 == 0) {
            // откат к старому поведению
            return legacyFixedTp(k, (int) getConfig());
        }

        // --- 3. Динамический коэффициент
        double volRatio = atr20 / atr100;
        volRatio = Math.max(FLOOR, Math.min(CEIL, volRatio));

        int keltnerTopRatio = (int) getConfig();          // 80, как и было
        double kBase = (keltnerTopRatio / 100.0) * 2.0;   // 1.6 ATR
        double kDyn = kBase / volRatio;

        // --- 4. Итоговый TP
        return middle + kDyn * atr20;
    }

    /**
     * fallback: поведение старого класса, если ATR ещё не рассчитан
     */
    private double legacyFixedTp(Keltner k, int ratioPercent) {
        double middle = k.getMiddle();
        double diff = k.getTop() - middle;
        return middle + diff * ratioPercent / 100.0;
    }

    @Override
    public String toString() {
        return "TP [Adaptive] Keltner " + getConfig() + "% top";
    }
}

