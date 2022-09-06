package com.kishlaly.ta.analyze.tasks.blocks;

import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.SymbolData;

public interface TaskBlock {

    BlockResultJava check(SymbolData screen);

}
