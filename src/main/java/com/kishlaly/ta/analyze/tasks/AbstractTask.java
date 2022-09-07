package com.kishlaly.ta.analyze.tasks;

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlockJava;
import com.kishlaly.ta.analyze.tasks.blocks.commons.CommonBlock;
import com.kishlaly.ta.analyze.tasks.blocks.one.ScreenOneBlock;
import com.kishlaly.ta.analyze.tasks.blocks.two.ScreenTwoBlock;
import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.ScreensJava;
import com.kishlaly.ta.model.SymbolDataJava;

import java.util.List;
import java.util.stream.Collectors;

public class AbstractTask {

    public static BlockResultJava check(ScreensJava screens, List<TaskBlockJava> blocks) {

        SymbolDataJava screen1 = screens.getScreen1().copy();
        SymbolDataJava screen2 = screens.getScreen2().copy();

        List<TaskBlockJava> commonBlocks = blocks
                .stream()
                .filter(block -> block instanceof CommonBlock)
                .collect(Collectors.toList());

        boolean commonBlocksSucceded = true;
        BlockResultJava commonBlockLastResult = null;

        for (int i = 0; i < commonBlocks.size(); i++) {
            TaskBlockJava commonBlock = commonBlocks.get(i);
            BlockResultJava check1 = commonBlock.check(screen1);
            BlockResultJava check2 = commonBlock.check(screen2);
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
            commonBlockLastResult.setLastChartQuote(screen2.getLastQuote());
            return commonBlockLastResult;
        }

        List<TaskBlockJava> screenOneBlocks = blocks
                .stream()
                .filter(block -> block instanceof ScreenOneBlock)
                .collect(Collectors.toList());

        boolean screenOneAllBlocksValid = true;
        BlockResultJava screenOneResult = null;

        for (int i = 0; i < screenOneBlocks.size(); i++) {
            TaskBlockJava screenOneBlock = screenOneBlocks.get(i);
            screenOneResult = screenOneBlock.check(screen1);
            if (!screenOneResult.isOk()) {
                screenOneAllBlocksValid = false;
                break;
            }
        }
        if (!screenOneAllBlocksValid) {
            screenOneResult.setLastChartQuote(screen2.getLastQuote());
            return screenOneResult;
        }

        List<TaskBlockJava> screenTwoBlocks = blocks
                .stream()
                .filter(block -> block instanceof ScreenTwoBlock)
                .collect(Collectors.toList());
        BlockResultJava screenTwoResult = null;
        for (int i = 0; i < screenTwoBlocks.size(); i++) {
            TaskBlockJava screenTwoBlock = screenTwoBlocks.get(i);
            screenTwoResult = screenTwoBlock.check(screen2);
            if (!screenTwoResult.isOk()) {
                break;
            }
        }
        screenTwoResult.setLastChartQuote(screen2.getLastQuote());
        return screenTwoResult;
    }

}
