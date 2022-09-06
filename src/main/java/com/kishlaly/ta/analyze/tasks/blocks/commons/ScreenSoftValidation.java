package com.kishlaly.ta.analyze.tasks.blocks.commons;

import com.kishlaly.ta.model.BlockResultCodeJava;
import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.utils.Log;

import static com.kishlaly.ta.model.BlockResultCodeJava.NO_DATA_QUOTES;
import static com.kishlaly.ta.model.BlockResultCodeJava.OK;

public class ScreenSoftValidation implements CommonBlock {

    @Override
    public BlockResultJava check(SymbolDataJava screen) {
        if (screen.quotes.isEmpty() || screen.quotes.isEmpty()) {
            Log.addDebugLine("There are not enough quotes for " + screen.symbol);
            Log.recordCode(BlockResultCodeJava.NO_DATA_QUOTES, screen);
            return new BlockResultJava(null, NO_DATA_QUOTES);
        }
        return new BlockResultJava(null, OK);
    }

}
