package com.kishlaly.ta.analyze.tasks;

import com.kishlaly.ta.analyze.BlockResultCode;
import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.utils.Log;

import java.util.Comparator;

import static com.kishlaly.ta.analyze.BlockResultCode.*;

public class FirstTrustModel {

    public static class Config {
        public static int MONTHS = 3;
    }

    public static BlockResult buySignal(SymbolData screen_1, SymbolData screen_2) {
        if (screen_1.quotes.isEmpty() || screen_2.quotes.isEmpty()) {
            Log.addDebugLine("Недостаточно ценовых столбиков для " + screen_1.symbol);
            Log.recordCode(BlockResultCode.NO_DATA_QUOTES, screen_1);
            return new BlockResult(null, NO_DATA_QUOTES);
        }

        Quote lastChartQuote = screen_2.quotes.get(screen_2.quotes.size() - 1);
        Quote signal = lastChartQuote;

        // ищем минимум за последние MONTHS месяцев в одном из 10 последних столбиков
        int days = Config.MONTHS * 21;
        Quote nMonthsLow = screen_2.quotes.subList(screen_2.quotes.size() - days, screen_2.quotes.size())
                .stream()
                .min(Comparator.comparing(quote -> quote.getLow())).get();
        int nMonthsLowIndex = -1;
        for (int i = 0; i < screen_2.quotes.size(); i++) {
            if (screen_2.quotes.get(i).getTimestamp().compareTo(nMonthsLow.getTimestamp()) == 0) {
                nMonthsLowIndex = i;
                break;
            }
        }
        if (nMonthsLowIndex < 0) {
            Log.addDebugLine("Недостаточно ценовых столбиков для поиска шестмесячного минимума у " + screen_1.symbol);
            Log.recordCode(BlockResultCode.NO_DATA_QUOTES, screen_1);
            return new BlockResult(lastChartQuote, NO_DATA_QUOTES);
        }

        if (screen_2.quotes.size() - nMonthsLowIndex > 5) {
            Log.addDebugLine("Минимум обнаружен далеко от последних трех столбиков");
            Log.recordCode(N_MONTHS_LOW_IS_TOO_FAR, screen_1);
            return new BlockResult(lastChartQuote, N_MONTHS_LOW_IS_TOO_FAR);
        }

        if (nMonthsLowIndex + 2 >= screen_2.quotes.size()) {
            Log.addDebugLine("Минимум обнаружен слишком близко к правому краю");
            Log.recordCode(N_MONTHS_LOW_IS_TOO_CLOSE, screen_1);
            return new BlockResult(lastChartQuote, N_MONTHS_LOW_IS_TOO_CLOSE);
        }

        // ищем хотя бы два зеленый столбика после минимума
        Quote quote_1_afterMin = screen_2.quotes.get(screen_2.quotes.size() - nMonthsLowIndex + 1);
        Quote quote_2_afterMin = screen_2.quotes.get(screen_2.quotes.size() - nMonthsLowIndex + 2);
        boolean ascendingLastBars = quote_1_afterMin.getOpen() < quote_1_afterMin.getClose() && quote_2_afterMin.getOpen() < quote_2_afterMin.getClose();
        if (!ascendingLastBars) {
            Log.addDebugLine("После минимума не было роста двух столбиков");
            Log.recordCode(QUOTES_NOT_ASCENDING_AFTER_MIN, screen_1);
            return new BlockResult(lastChartQuote, QUOTES_NOT_ASCENDING_AFTER_MIN);
        }

        return new BlockResult(signal, OK);
    }

}
