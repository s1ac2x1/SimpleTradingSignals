package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResultCodeJava;
import com.kishlaly.ta.analyze.tasks.FirstTrustModel;
import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.QuoteJava;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.utils.Log;

import java.util.Comparator;

import static com.kishlaly.ta.model.BlockResultCodeJava.*;

public class Long_ScreenTwo_FirstTrustModelMainLogic implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        QuoteJava lastChartQuote = screen.quotes.get(screen.quotes.size() - 1);
        QuoteJava signal = lastChartQuote;

        // look for the minimum for the last MONTHS months in one of the last 10 columns
        int days = FirstTrustModel.Config.MONTHS * 21;
        QuoteJava nMonthsLow = screen.quotes.subList(screen.quotes.size() - days, screen.quotes.size())
                .stream()
                .min(Comparator.comparing(quote -> quote.getLow())).get();
        int nMonthsLowIndex = -1;
        for (int i = 0; i < screen.quotes.size(); i++) {
            if (screen.quotes.get(i).getTimestamp().compareTo(nMonthsLow.getTimestamp()) == 0) {
                nMonthsLowIndex = i;
                break;
            }
        }
        if (nMonthsLowIndex < 0) {
            Log.addDebugLine("Not enough price bars to find a six-month low at " + screen.symbol);
            Log.recordCode(BlockResultCodeJava.NO_DATA_QUOTES, screen);
            return new BlockResult(lastChartQuote, NO_DATA_QUOTES);
        }

        if (screen.quotes.size() - nMonthsLowIndex > 5) {
            Log.addDebugLine("The minimum is found far from the last three bars");
            Log.recordCode(N_MONTHS_LOW_IS_TOO_FAR_SCREEN_2, screen);
            return new BlockResult(lastChartQuote, N_MONTHS_LOW_IS_TOO_FAR_SCREEN_2);
        }

        if (nMonthsLowIndex + 2 >= screen.quotes.size()) {
            Log.addDebugLine("Minimum detected too close to the right edge");
            Log.recordCode(N_MONTHS_LOW_IS_TOO_CLOSE_SCREEN_2, screen);
            return new BlockResult(lastChartQuote, N_MONTHS_LOW_IS_TOO_CLOSE_SCREEN_2);
        }

        // looking for at least two green bars after the minimum
        QuoteJava quote_1_afterMin = screen.quotes.get(screen.quotes.size() - nMonthsLowIndex + 1);
        QuoteJava quote_2_afterMin = screen.quotes.get(screen.quotes.size() - nMonthsLowIndex + 2);
        boolean ascendingLastBars = quote_1_afterMin.getOpen() < quote_1_afterMin.getClose() && quote_2_afterMin.getOpen() < quote_2_afterMin.getClose();
        if (!ascendingLastBars) {
            Log.addDebugLine("After the minimum there was no growth of two bars");
            Log.recordCode(QUOTES_NOT_ASCENDING_AFTER_MIN, screen);
            return new BlockResult(lastChartQuote, QUOTES_NOT_ASCENDING_AFTER_MIN);
        }

        return new BlockResult(signal, OK);
    }
}
