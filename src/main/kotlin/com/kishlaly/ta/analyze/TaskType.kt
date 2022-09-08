package com.kishlaly.ta.analyze

import com.kishlaly.ta.analyze.tasks.AbstractTask
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock
import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.Screens
import com.kishlaly.ta.model.Timeframe
import com.kishlaly.ta.model.Timeframe.DAY
import com.kishlaly.ta.model.Timeframe.WEEK
import com.kishlaly.ta.model.TimeframeIndicators
import com.kishlaly.ta.model.indicators.Indicator
import com.kishlaly.ta.model.indicators.Indicator.*
import java.util.function.BiFunction

enum class TaskType(
        var timeframes: MutableMap<Int, Timeframe>,
        var indicators: MutableMap<Int, Array<Indicator>>,
        var function: BiFunction<Screens, List<TaskBlock>, BlockResult>) {

    MACD_BULLISH_DIVERGENCE(
            mutableMapOf(1 to WEEK, 2 to DAY),
            mutableMapOf(1 to arrayOf(EMA26, MACD), 2 to arrayOf(MACD, KELTNER)),
            AbstractTask::check
    ),
    THREE_DISPLAYS_BUY(
            mutableMapOf(1 to WEEK, 2 to DAY),
            mutableMapOf(1 to arrayOf(EMA26, MACD), 2 to arrayOf(EMA13, MACD, STOCH, KELTNER, BOLLINGER, EFI)),
            AbstractTask::check
    ),
    THREE_DISPLAYS_SELL(
            mutableMapOf(1 to WEEK, 2 to DAY),
            mutableMapOf(1 to arrayOf(EMA26, MACD), 2 to arrayOf(EMA13, MACD, STOCH, KELTNER, BOLLINGER, EFI)),
            AbstractTask::check
    ),
    FIRST_TRUST_MODEL(
            mutableMapOf(1 to WEEK, 2 to DAY),
            mutableMapOf(1 to arrayOf(EMA26, MACD), 2 to arrayOf(EMA13, MACD, STOCH, KELTNER)),
            AbstractTask::check
    );

    fun updateTimeframeForScreen(screen: Int, timeframe: Timeframe) {
        timeframes.put(screen, timeframe)
    }

    fun updateIndicatorsForScreen(screen: Int, indicators: Array<Indicator>) {
        this.indicators[screen] = indicators
    }

    fun getTimeframeIndicators(screen: Int): TimeframeIndicators {
        return TimeframeIndicators(timeframes[screen]!!, indicators[screen]!!)
    }

}