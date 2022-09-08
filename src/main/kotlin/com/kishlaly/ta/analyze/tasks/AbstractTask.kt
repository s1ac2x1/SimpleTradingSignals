package com.kishlaly.ta.analyze.tasks

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock
import com.kishlaly.ta.analyze.tasks.blocks.commons.CommonBlock
import com.kishlaly.ta.analyze.tasks.blocks.one.ScreenOneBlock
import com.kishlaly.ta.analyze.tasks.blocks.two.ScreenTwoBlock
import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.Screens

abstract class AbstractTask {

    companion object {

        fun check(screens: Screens, blocks: List<TaskBlock>): BlockResult {
            val screen1 = screens.screen1.copy()
            val screen2 = screens.screen2.copy()

            // common blocks
            val commonBlocks = blocks.filter { it is CommonBlock }.toList()
            var commonBlocksSucceded = true

            var commonBlockLastResult: BlockResult? = null
            for (i in commonBlocks.indices) {
                val commonBlock = commonBlocks[i]
                val check1 = commonBlock.check(screen1)
                val check2 = commonBlock.check(screen2)
                if (!check1.isOk()) {
                    commonBlockLastResult = check1
                    commonBlocksSucceded = false
                }
                if (!check2.isOk()) {
                    commonBlockLastResult = check2
                    commonBlocksSucceded = false
                }
            }
            if (!commonBlocksSucceded) {
                commonBlockLastResult?.lastChartQuote = screen2.lastQuote;
                return commonBlockLastResult!!
            }

            // screen 1 blocks
            val screenOneBlocks = blocks.filter { it is ScreenOneBlock }.toList()
            var screenOneAllBlocksValid = true
            var screenOneResult: BlockResult? = null

            for (i in screenOneBlocks.indices) {
                val screenOneBlock = screenOneBlocks[i]
                screenOneResult = screenOneBlock.check(screen1)
                if (!screenOneResult.isOk()) {
                    screenOneAllBlocksValid = false
                    break
                }
            }
            if (!screenOneAllBlocksValid) {
                screenOneResult?.lastChartQuote = screen2.lastQuote
                return screenOneResult!!
            }

            // screen 2
            val screenTwoBlocks = blocks.filter { it is ScreenTwoBlock }.toList()
            var screenTwoResult: BlockResult? = null
            for (i in screenTwoBlocks.indices) {
                val screenTwoBlock = screenTwoBlocks[i]
                screenTwoResult = screenTwoBlock.check(screen2)
                if (!screenTwoResult.isOk()) {
                    break
                }
            }

            screenTwoResult?.lastChartQuote = screen2.lastQuote
            return screenTwoResult!!
        }

    }

}