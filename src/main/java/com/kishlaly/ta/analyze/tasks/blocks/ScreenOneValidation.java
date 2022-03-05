package com.kishlaly.ta.analyze.tasks.blocks;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.utils.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.kishlaly.ta.analyze.BlockResultCode.*;
import static com.kishlaly.ta.utils.Quotes.resolveMinBarsCount;

public class ScreenOneValidation implements ScreenOneBlock {

    @Override
    public BlockResult check(SymbolData screenOne) {
        if (screenOne.quotes.isEmpty() || screenOne.quotes.size() < resolveMinBarsCount(screenOne.timeframe)) {
            Log.addDebugLine("Недостаточно ценовых столбиков для " + screenOne.timeframe.name());
            Log.recordCode(NO_DATA_QUOTES, screenOne);
            return new BlockResult(null, NO_DATA_QUOTES);
        }

        List<Indicator> missingData = new ArrayList<>();
        screenOne.indicators.forEach((indicator, value) -> {
            if (value.isEmpty() || value.size() < resolveMinBarsCount(screenOne.timeframe)) {
                missingData.add(indicator);
            }
        });
        if (!missingData.isEmpty()) {
            Log.recordCode(NO_DATA_INDICATORS, screenOne);
            Log.addDebugLine("Нету данных по индикаторам: " + missingData.stream().map(indicator -> indicator.name()).collect(Collectors.joining(", ")));
            return new BlockResult(null, NO_DATA_INDICATORS);
        }

        return new BlockResult(null, OK);
    }

}
