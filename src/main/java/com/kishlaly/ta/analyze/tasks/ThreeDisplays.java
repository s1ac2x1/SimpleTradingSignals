package com.kishlaly.ta.analyze.tasks;

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.analyze.tasks.blocks.one.ScreenOneBlock;
import com.kishlaly.ta.analyze.tasks.blocks.two.ScreenTwoBlock;
import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.Screens;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.utils.IndicatorUtils;
import com.kishlaly.ta.utils.Quotes;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Индикаторы:
 * EMA26 (close) на первом экране
 * MACD (12 26 9 close) на втором экране
 * EMA13 (close) на втором экране
 * STOCH (14 1 3 close) на втором экране
 */
public class ThreeDisplays {

    public static class Config {

        // 3 дает меньше сигналов, но они надежнее
        public static int NUMBER_OF_EMA26_VALUES_TO_CHECK = 4;

        public static int STOCH_OVERSOLD = 40;
        public static int STOCH_OVERBOUGHT = 70;
        public static int STOCH_VALUES_TO_CHECK = 5;

        // в процентах от середины до вершины канала
        public static int FILTER_BY_KELTNER = 40;

        // фильтрация сигналов, если котировка закрылась выше FILTER_BY_KELTNER
        // тесты показывают результат лучше, когда эта проверка выключена
        public static boolean FILTER_BY_KELTNER_ENABLED;
    }

    private static BlockResult check(Screens screens, List<TaskBlock> blocks) {

        SymbolData screen1 = screens.getScreen1();
        SymbolData screen2 = screens.getScreen2();

        Quotes.trim(screen1);
        Quotes.trim(screen2);
        IndicatorUtils.trim(screen1);
        IndicatorUtils.trim(screen2);

        List<TaskBlock> screenOneBlocks = blocks
                .stream()
                .filter(block -> block instanceof ScreenOneBlock)
                .collect(Collectors.toList());

        boolean screenOneAllBlocksValid = true;
        BlockResult screenOneResult = null;

        for (int i = 0; i < screenOneBlocks.size(); i++) {
            TaskBlock screenOneBlock = screenOneBlocks.get(i);
            screenOneResult = screenOneBlock.check(screen1);
            if (!screenOneResult.isOk()) {
                screenOneAllBlocksValid = false;
                break;
            }
        }
        if (!screenOneAllBlocksValid) {
            return screenOneResult;
        }

        List<TaskBlock> screenTwoBlocks = blocks
                .stream()
                .filter(block -> block instanceof ScreenTwoBlock)
                .collect(Collectors.toList());
        BlockResult screenTwoResult = null;
        for (int i = 0; i < screenTwoBlocks.size(); i++) {
            TaskBlock screenTwoBlock = screenTwoBlocks.get(i);
            screenTwoResult = screenTwoBlock.check(screen2);
            if (!screenTwoResult.isOk()) {
                break;
            }
        }
        return screenTwoResult;
    }

    public static BlockResult buy(Screens screens, List<TaskBlock> blocks) {
        return check(screens, blocks);
    }

    public static BlockResult sell(Screens screens, List<TaskBlock> blocks) {
        return check(screens, blocks);
    }

}
