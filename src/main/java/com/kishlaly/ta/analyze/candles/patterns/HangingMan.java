package com.kishlaly.ta.analyze.candles.patterns;

import com.kishlaly.ta.analyze.candles.CandlePattern;
import com.kishlaly.ta.analyze.candles.CandleResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.EMAJava;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.utils.IndicatorUtils;

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
    public CandleResult check(SymbolData screen) {
        // EMA rises
        if (IndicatorUtils.emaAscending((List<EMAJava>) screen.indicators.get(Indicator.EMA13), 3, 4)) {
            // The body is in the upper part of the price range

        }

        return CandleResult.NO_RESULT;
    }

}
