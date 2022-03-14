package com.kishlaly.ta.analyze.tasks;

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.analyze.tasks.blocks.commons.CommonBlock;
import com.kishlaly.ta.analyze.tasks.blocks.one.ScreenOneBlock;
import com.kishlaly.ta.analyze.tasks.blocks.two.ScreenTwoBlock;
import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.Screens;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.utils.IndicatorUtils;
import com.kishlaly.ta.utils.Quotes;

import java.util.List;
import java.util.stream.Collectors;

public class AbstractTask {

    public static BlockResult check(Screens screens, List<TaskBlock> blocks) {

        SymbolData screen1 = screens.getScreen1();
        SymbolData screen2 = screens.getScreen2();

        Quotes.trim(screen1);
        Quotes.trim(screen2);
        IndicatorUtils.trim(screen1);
        IndicatorUtils.trim(screen2);

        List<TaskBlock> commonBlocks = blocks
                .stream()
                .filter(block -> block instanceof CommonBlock)
                .collect(Collectors.toList());

        boolean commonBlocksSucceded = true;
        BlockResult commonBlockLastResult = null;

        for (int i = 0; i < commonBlocks.size(); i++) {
            TaskBlock commonBlock = commonBlocks.get(i);
            BlockResult check1 = commonBlock.check(screen1);
            BlockResult check2 = commonBlock.check(screen2);
            if (!check1.isOk()) {
                commonBlockLastResult = check1;
                commonBlocksSucceded = false;
            }
            if (!check2.isOk()) {
                commonBlockLastResult = check2;
                commonBlocksSucceded = false;
            }
        }
        if (!commonBlocksSucceded) {
            return commonBlockLastResult;
        }

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

}
