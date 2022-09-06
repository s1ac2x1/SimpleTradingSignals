package com.kishlaly.ta.analyze.candles.patterns;

import com.kishlaly.ta.analyze.candles.CandlePattern;
import com.kishlaly.ta.analyze.candles.CandleResult;
import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.model.indicators.EMAJava;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.utils.IndicatorUtilsJava;

import java.util.List;

/**
 * The situation: the bullish trend becomes susceptible to new sellers
 * Baseline signs:
 * + EMA grows
 * + Color does not matter
 * + Body is in the upper part of the price range
 * + Lower shadow is twice as long as the body
 * + Upper shadow is absent or very short
 * Additional signs:
 * + Next quote is red (optional)
 * + Next quote opened below the hanging candle body (optional, but a good sign)
 */
//TODO finish
public class HangingMan implements CandlePattern {

    @Override
    public CandleResult check(SymbolDataJava screen) {
        // EMA rises
        if (IndicatorUtilsJava.emaAscending((List<EMAJava>) screen.indicators.get(IndicatorJava.EMA13), 3, 4)) {
            // The body is in the upper part of the price range

        }

        return CandleResult.NO_RESULT;
    }

}
