package com.kishlaly.ta.analyze.tasks.blocks;

import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.SymbolDataJava;

public interface TaskBlockJava {

    BlockResultJava check(SymbolDataJava screen);

}
