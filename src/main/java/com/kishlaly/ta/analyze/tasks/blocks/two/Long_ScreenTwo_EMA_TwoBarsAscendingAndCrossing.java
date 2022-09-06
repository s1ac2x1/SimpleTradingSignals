package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.QuoteJava;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.EMAJava;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.utils.CollectionsTools;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.*;
import static com.kishlaly.ta.utils.Quotes.*;

/**
 * Quotes should cross EMA13 and should go up
 */
public class Long_ScreenTwo_EMA_TwoBarsAscendingAndCrossing implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<QuoteJava> screen_2_Quotes = screen.quotes;
        List<EMAJava> screen_2_EMA13 = (List<EMAJava>) screen.indicators.get(Indicator.EMA13);
        // prerequisite 1:
        // make sure first that the last TWO columns go up
        QuoteJava preLastQuote = CollectionsTools.getFromEnd(screen_2_Quotes, 2);
        QuoteJava lastQuote = CollectionsTools.getFromEnd(screen_2_Quotes, 1);
        boolean ascendingBarHigh = preLastQuote.getHigh() < lastQuote.getHigh();
        if (!ascendingBarHigh) {
            Log.recordCode(QUOTE_HIGH_NOT_GROWING_SCREEN_2, screen);
            Log.addDebugLine("Quote.high does not grow consistently");
            return new BlockResult(screen.getLastQuote(), QUOTE_HIGH_NOT_GROWING_SCREEN_2);
        }
        EMAJava preLastEMA = CollectionsTools.getFromEnd(screen_2_EMA13, 2);
        EMAJava lastEMA = CollectionsTools.getFromEnd(screen_2_EMA13, 1);

        // both quotes below EMA - reject
        if (isQuoteBelowEMA(preLastQuote, preLastEMA.getValue()) && isQuoteBelowEMA(lastQuote, lastEMA.getValue())) {
            Log.addDebugLine("Both last two bars are below the EMA");
            Log.recordCode(QUOTES_BELOW_EMA_SCREEN_2, screen);
            return new BlockResult(screen.getLastQuote(), QUOTES_BELOW_EMA_SCREEN_2);
        }

        // both quotes above the EMA - reject
        if (isQuoteAboveEMA(preLastQuote, preLastEMA.getValue()) && isQuoteAboveEMA(lastQuote, lastEMA.getValue())) {
            Log.addDebugLine("Both last two bars are above the EMA");
            Log.recordCode(QUOTES_ABOVE_EMA_SCREEN_2, screen);
            return new BlockResult(screen.getLastQuote(), QUOTES_ABOVE_EMA_SCREEN_2);
        }

        // the penultimate one is below the EMA, the last one crosses or is above it - OK
        boolean crossingRule1 = isQuoteBelowEMA(preLastQuote, preLastEMA.getValue())
                && (isQuoteCrossedEMA(lastQuote, lastEMA.getValue()) || isQuoteAboveEMA(lastQuote, lastEMA.getValue()));

        // the penultimate one is below the EMA, the last one crosses - OK
        boolean crossingRule2 = isQuoteBelowEMA(preLastQuote, preLastEMA.getValue()) && isQuoteCrossedEMA(lastQuote, lastEMA.getValue());

        // penultimate and last cross the EMA - OK
        boolean crossingRule3 = isQuoteCrossedEMA(preLastQuote, preLastEMA.getValue()) && isQuoteCrossedEMA(lastQuote, lastEMA.getValue());

        // the penultimate one crosses the EMA, the last one is higher (it may be too late to enter the trade, we need to look at the chart) - OK
        boolean crossingRule4 = isQuoteCrossedEMA(preLastQuote, preLastEMA.getValue()) && isQuoteAboveEMA(lastQuote, lastEMA.getValue());

        boolean crossingOk = crossingRule1 || crossingRule2 || crossingRule3 || crossingRule4;
        if (!crossingOk) {
            Log.addDebugLine("The rule of EMA crossing is not fulfilled");
            Log.recordCode(CROSSING_RULE_VIOLATED_SCREEN_2, screen);
            return new BlockResult(screen.getLastQuote(), CROSSING_RULE_VIOLATED_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
