package com.kishlaly.ta.analyze.tasks;

import com.kishlaly.ta.analyze.SignalResultCode;
import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.utils.Log;

public class FirstTrustModel {

    public static Quote buySignal(SymbolData data) {
        Quote signal = null;
        if (data.quotes.isEmpty()) {
            Log.addDebugLine("Недостаточно ценовых столбиков для " + data.timeframe.name());
            Log.recordCode(SignalResultCode.NO_DATA_QUOTES, data);
            return null;
        }

        // ищем минимум за последние 6 месяцев в одном из 10 последних столбиков


        return signal;
    }

}
