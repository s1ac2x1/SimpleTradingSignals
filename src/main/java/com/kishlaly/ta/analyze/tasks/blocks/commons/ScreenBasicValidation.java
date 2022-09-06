package com.kishlaly.ta.analyze.tasks.blocks.commons;

import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.utils.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.kishlaly.ta.model.BlockResultCodeJava.*;
import static com.kishlaly.ta.utils.Quotes.resolveMinBarsCount;

public class ScreenBasicValidation implements CommonBlock {

    @Override
    public BlockResultJava check(SymbolDataJava screen) {
        if (screen.quotes.isEmpty() || screen.quotes.size() < resolveMinBarsCount(screen.timeframe)) {
            Log.addDebugLine("There are not enough quotes for " + screen.timeframe.name());
            Log.recordCode(NO_DATA_QUOTES, screen);
            return new BlockResultJava(null, NO_DATA_QUOTES);
        }

        List<IndicatorJava> missingData = new ArrayList<>();
        screen.indicators.forEach((indicator, value) -> {
            if (value.isEmpty() || value.size() < resolveMinBarsCount(screen.timeframe)) {
                missingData.add(indicator);
            }
        });
        if (!missingData.isEmpty()) {
            Log.recordCode(NO_DATA_INDICATORS, screen);
            Log.addDebugLine("No indicator data: " + missingData.stream().map(indicator -> indicator.name()).collect(Collectors.joining(", ")));
            return new BlockResultJava(null, NO_DATA_INDICATORS);
        }

        return new BlockResultJava(null, OK);
    }

}
