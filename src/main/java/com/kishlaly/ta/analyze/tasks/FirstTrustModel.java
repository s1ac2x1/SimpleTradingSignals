package com.kishlaly.ta.analyze.tasks;

import com.kishlaly.ta.analyze.TaskResultCode;
import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.TaskResult;
import com.kishlaly.ta.utils.Log;

import java.util.Comparator;

import static com.kishlaly.ta.analyze.TaskResultCode.*;

public class FirstTrustModel {

    public static TaskResult buySignal(SymbolData screen_1, SymbolData screen_2) {
        Quote signal = null;
        if (screen_1.quotes.isEmpty() || screen_2.quotes.isEmpty()) {
            Log.addDebugLine("Недостаточно ценовых столбиков для " + screen_1.symbol);
            Log.recordCode(TaskResultCode.NO_DATA_QUOTES, screen_1);
            return new TaskResult(null, NO_DATA_QUOTES);
        }

        Quote lastChartQuote = screen_2.quotes.get(screen_2.quotes.size() - 1);

        // ищем минимум за последние 6 месяцев в одном из 10 последних столбиков
        Quote sixMonthsLow = screen_2.quotes.subList(screen_2.quotes.size() - 126, screen_2.quotes.size())
                .stream()
                .min(Comparator.comparing(quote -> quote.getLow())).get();
        int sixMonthsLowIndex = -1;
        for (int i = 0; i < screen_2.quotes.size(); i++) {
            if (screen_2.quotes.get(i).getTimestamp().compareTo(sixMonthsLow.getTimestamp()) == 0) {
                sixMonthsLowIndex = i;
                break;
            }
        }
        if (sixMonthsLowIndex < 0) {
            Log.addDebugLine("Недостаточно ценовых столбиков для поиска шестмесячного минимума у " + screen_1.symbol);
            Log.recordCode(TaskResultCode.NO_DATA_QUOTES, screen_1);
            return new TaskResult(lastChartQuote, NO_DATA_QUOTES);
        }

        if (screen_2.quotes.size() - sixMonthsLowIndex > 10) {
            Log.addDebugLine("Минимум обнаружен далеко от последних десяти столбиков");
            Log.recordCode(SIX_MONTHS_LOW_IS_FAR, screen_1);
            return new TaskResult(lastChartQuote, SIX_MONTHS_LOW_IS_FAR);
        }

        return new TaskResult(signal, SIGNAL);
    }

}
