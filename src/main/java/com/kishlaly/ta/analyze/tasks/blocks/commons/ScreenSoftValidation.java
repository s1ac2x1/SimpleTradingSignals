package com.kishlaly.ta.analyze.tasks.blocks.commons;

import com.kishlaly.ta.analyze.BlockResultCode;
import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.utils.Log;

import static com.kishlaly.ta.analyze.BlockResultCode.NO_DATA_QUOTES;
import static com.kishlaly.ta.analyze.BlockResultCode.OK;

public class ScreenSoftValidation implements CommonBlock {

    @Override
    public BlockResult check(SymbolData screen) {
        if (screen.quotes.isEmpty() || screen.quotes.isEmpty()) {
            Log.addDebugLine("Недостаточно ценовых столбиков для " + screen.symbol);
            Log.recordCode(BlockResultCode.NO_DATA_QUOTES, screen);
            return new BlockResult(null, NO_DATA_QUOTES);
        }
        return new BlockResult(null, OK);
    }

}
