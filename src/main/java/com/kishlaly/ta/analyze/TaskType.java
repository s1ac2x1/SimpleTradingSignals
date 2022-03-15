package com.kishlaly.ta.analyze;

import com.kishlaly.ta.analyze.tasks.AbstractTask;
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.Screens;
import com.kishlaly.ta.model.Timeframe;
import com.kishlaly.ta.model.TimeframeIndicators;
import com.kishlaly.ta.model.indicators.Indicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static com.kishlaly.ta.model.Timeframe.DAY;
import static com.kishlaly.ta.model.Timeframe.WEEK;
import static com.kishlaly.ta.model.indicators.Indicator.*;

public enum TaskType {
    MACD_BULLISH_DIVERGENCE(
            new HashMap<Integer, Timeframe>() {{
                put(1, WEEK);
                put(2, DAY);
            }},
            new HashMap<Integer, Indicator[]>() {{
                put(1, new Indicator[]{EMA26, MACD});
                put(2, new Indicator[]{MACD, KELTNER});
            }},
            AbstractTask::check
    ),
    THREE_DISPLAYS_BUY(
            new HashMap<Integer, Timeframe>() {{
                put(1, WEEK);
                put(2, DAY);
            }},
            new HashMap<Integer, Indicator[]>() {{
                put(1, new Indicator[]{EMA26, MACD});
                put(2, new Indicator[]{EMA13, MACD, STOCH, KELTNER});
            }},
            AbstractTask::check
    ),
    THREE_DISPLAYS_SELL(
            new HashMap<Integer, Timeframe>() {{
                put(1, WEEK);
                put(2, DAY);
            }},
            new HashMap<Integer, Indicator[]>() {{
                put(1, new Indicator[]{EMA26, MACD});
                put(2, new Indicator[]{EMA13, MACD, STOCH, KELTNER});
            }},
            AbstractTask::check
    ),
    FIRST_TRUST_MODEL(
            new HashMap<Integer, Timeframe>() {{
                put(1, WEEK);
                put(2, DAY);
            }},
            new HashMap<Integer, Indicator[]>() {{
                put(1, new Indicator[]{EMA26, MACD});
                put(2, new Indicator[]{EMA13, MACD, STOCH, KELTNER});
            }},
            AbstractTask::check
    );

    TaskType(final Map<Integer, Timeframe> timeframes,
             final Map<Integer, Indicator[]> indicators,
             BiFunction<Screens, List<TaskBlock>, BlockResult> function) {
        this.timeframes = timeframes;
        this.indicators = indicators;
        this.function = function;
    }

    private Map<Integer, Timeframe> timeframes;
    private Map<Integer, Indicator[]> indicators;
    private BiFunction<Screens, List<TaskBlock>, BlockResult> function;
    private TimeframeIndicators timeframeIndicators;
    private List<TaskBlock> blocks = new ArrayList<>();

    public List<TaskBlock> getBlocks() {
        return this.blocks;
    }

    public void setBlocks(final List<TaskBlock> blocks) {
        this.blocks = blocks;
    }

    public Timeframe getTimeframeForScreen(int screen) {
        return timeframes.get(screen);
    }

    public Indicator[] getIndicatorsForScreen(int screen) {
        return indicators.get(screen);
    }

    public void updateTimeframeForScreen(int screen, Timeframe timeframe) {
        timeframes.put(screen, timeframe);
    }

    public void updateIndicatorsForScreen(int screen, Indicator[] indicators) {
        this.indicators.put(screen, indicators);
    }

    public BiFunction<Screens, List<TaskBlock>, BlockResult> getFunction() {
        return this.function;
    }

    public TimeframeIndicators getTimeframeIndicators(int screen) {
        return new TimeframeIndicators(
                timeframes.get(screen),
                indicators.get(screen)
        );
    }

}
