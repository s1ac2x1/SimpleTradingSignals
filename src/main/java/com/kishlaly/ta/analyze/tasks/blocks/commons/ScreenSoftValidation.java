package com.kishlaly.ta.analyze.tasks.blocks.commons;

import com.kishlaly.ta.model.BlockResultCodeJava;
import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.utils.Log;

import static com.kishlaly.ta.model.BlockResultCodeJava.NO_DATA_QUOTES;
import static com.kishlaly.ta.model.BlockResultCodeJava.OK;

public class ScreenSoftValidation implements CommonBlock {

    @Override
    public BlockResult check(SymbolData screen) {
        if (screen.quotes.isEmpty() || screen.quotes.isEmpty()) {
            Log.addDebugLine("There are not enough quotes for " + screen.symbol);
            Log.recordCode(BlockResultCodeJava.NO_DATA_QUOTES, screen);
            return new BlockResult(null, NO_DATA_QUOTES);
        }
        return new BlockResult(null, OK);
    }

}
