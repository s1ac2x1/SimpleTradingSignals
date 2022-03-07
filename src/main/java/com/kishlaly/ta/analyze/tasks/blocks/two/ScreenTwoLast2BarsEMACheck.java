package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;

/**
 * гистограмма должна быть ниже нуля и начать повышаться: проверить на ДВУХ последних значениях
 */
public class ScreenTwoLast2BarsEMACheck implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        return null;
    }
}
