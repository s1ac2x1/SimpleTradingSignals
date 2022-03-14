package com.kishlaly.ta.analyze.tasks.blocks;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;

public interface TaskBlock {

    BlockResult check(SymbolData screen);

}
