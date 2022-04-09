package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.analyze.BlockResultCode;
import com.kishlaly.ta.analyze.tasks.FirstTrustModel;
import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.utils.Log;

import java.util.Comparator;

import static com.kishlaly.ta.analyze.BlockResultCode.*;

public class Long_ScreenTwo_FirstTrustModelMainLogic implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        Quote lastChartQuote = screen.quotes.get(screen.quotes.size() - 1);
        Quote signal = lastChartQuote;

        // ищем минимум за последние MONTHS месяцев в одном из 10 последних столбиков
        int days = FirstTrustModel.Config.MONTHS * 21;
        Quote nMonthsLow = screen.quotes.subList(screen.quotes.size() - days, screen.quotes.size())
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
            Log.addDebugLine("Недостаточно ценовых столбиков для поиска шестмесячного минимума у " + screen.symbol);
            Log.recordCode(BlockResultCode.NO_DATA_QUOTES, screen);
            return new BlockResult(lastChartQuote, NO_DATA_QUOTES);
        }

        if (screen.quotes.size() - nMonthsLowIndex > 5) {
            Log.addDebugLine("Минимум обнаружен далеко от последних трех столбиков");
            Log.recordCode(N_MONTHS_LOW_IS_TOO_FAR_SCREEN_2, screen);
            return new BlockResult(lastChartQuote, N_MONTHS_LOW_IS_TOO_FAR_SCREEN_2);
        }

        if (nMonthsLowIndex + 2 >= screen.quotes.size()) {
            Log.addDebugLine("Минимум обнаружен слишком близко к правому краю");
            Log.recordCode(N_MONTHS_LOW_IS_TOO_CLOSE_SCREEN_2, screen);
            return new BlockResult(lastChartQuote, N_MONTHS_LOW_IS_TOO_CLOSE_SCREEN_2);
        }

        // ищем хотя бы два зеленый столбика после минимума
        Quote quote_1_afterMin = screen.quotes.get(screen.quotes.size() - nMonthsLowIndex + 1);
        Quote quote_2_afterMin = screen.quotes.get(screen.quotes.size() - nMonthsLowIndex + 2);
        boolean ascendingLastBars = quote_1_afterMin.getOpen() < quote_1_afterMin.getClose() && quote_2_afterMin.getOpen() < quote_2_afterMin.getClose();
        if (!ascendingLastBars) {
            Log.addDebugLine("После минимума не было роста двух столбиков");
            Log.recordCode(QUOTES_NOT_ASCENDING_AFTER_MIN, screen);
            return new BlockResult(lastChartQuote, QUOTES_NOT_ASCENDING_AFTER_MIN);
        }

        return new BlockResult(signal, OK);
    }
}
