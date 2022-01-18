package com.kishlaly.ta.analyze.tasks;

import com.kishlaly.ta.analyze.TaskResultCode;
import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.TaskResult;
import com.kishlaly.ta.utils.Log;

import static com.kishlaly.ta.analyze.TaskResultCode.NO_DATA_QUOTES;
import static com.kishlaly.ta.analyze.TaskResultCode.SIGNAL;

public class FirstTrustModel {

    public static TaskResult buySignal(SymbolData screen_1, SymbolData screen_2) {
        Quote signal = null;
        if (screen_1.quotes.isEmpty() || screen_2.quotes.isEmpty()) {
            Log.addDebugLine("Недостаточно ценовых столбиков для " + screen_1.symbol);
            Log.recordCode(TaskResultCode.NO_DATA_QUOTES, screen_1);
            return new TaskResult(null, NO_DATA_QUOTES);
        }

        // ищем минимум за последние 6 месяцев в одном из 10 последних столбиков
//        screen_2.quotes.subList(screen_2.quotes.size() - 126, screen_2.quotes.size())
//                .stream()
//                .

        return new TaskResult(signal, SIGNAL);
    }

}
