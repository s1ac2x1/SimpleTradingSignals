package com.kishlaly.ta.analyze;

import com.kishlaly.ta.analyze.tasks.AbstractTask;
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.Screens;
import com.kishlaly.ta.model.TimeframeJava;
import com.kishlaly.ta.model.TimeframeIndicatorsJava;
import com.kishlaly.ta.model.indicators.IndicatorJava;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static com.kishlaly.ta.model.TimeframeJava.DAY;
import static com.kishlaly.ta.model.TimeframeJava.WEEK;
import static com.kishlaly.ta.model.indicators.IndicatorJava.*;

public enum TaskType {
    MACD_BULLISH_DIVERGENCE(
            new HashMap<Integer, TimeframeJava>() {{
                put(1, WEEK);
                put(2, DAY);
            }},
            new HashMap<Integer, IndicatorJava[]>() {{
                put(1, new IndicatorJava[]{EMA26, MACD});
                put(2, new IndicatorJava[]{MACD, KELTNER});
            }},
            AbstractTask::check
    ),
    THREE_DISPLAYS_BUY(
            new HashMap<Integer, TimeframeJava>() {{
                put(1, WEEK);
                put(2, DAY);
            }},
            new HashMap<Integer, IndicatorJava[]>() {{
                put(1, new IndicatorJava[]{EMA26, MACD});
                put(2, new IndicatorJava[]{EMA13, MACD, STOCH, KELTNER, BOLLINGER, EFI});
            }},
            AbstractTask::check
    ),
    THREE_DISPLAYS_SELL(
            new HashMap<Integer, TimeframeJava>() {{
                put(1, WEEK);
                put(2, DAY);
            }},
            new HashMap<Integer, IndicatorJava[]>() {{
                put(1, new IndicatorJava[]{EMA26, MACD});
                put(2, new IndicatorJava[]{EMA13, MACD, STOCH, KELTNER, BOLLINGER, EFI});
            }},
            AbstractTask::check
    ),
    FIRST_TRUST_MODEL(
            new HashMap<Integer, TimeframeJava>() {{
                put(1, WEEK);
                put(2, DAY);
            }},
            new HashMap<Integer, IndicatorJava[]>() {{
                put(1, new IndicatorJava[]{EMA26, MACD});
                put(2, new IndicatorJava[]{EMA13, MACD, STOCH, KELTNER});
            }},
            AbstractTask::check
    );

    TaskType(final Map<Integer, TimeframeJava> timeframes,
             final Map<Integer, IndicatorJava[]> indicators,
             BiFunction<Screens, List<TaskBlock>, BlockResultJava> function) {
        this.timeframes = timeframes;
        this.indicators = indicators;
        this.function = function;
    }

    private Map<Integer, TimeframeJava> timeframes;
    private Map<Integer, IndicatorJava[]> indicators;
    private BiFunction<Screens, List<TaskBlock>, BlockResultJava> function;
    private TimeframeIndicatorsJava timeframeIndicators;
    private List<TaskBlock> blocks = new ArrayList<>();

    public List<TaskBlock> getBlocks() {
        return this.blocks;
    }

    public void setBlocks(final List<TaskBlock> blocks) {
        this.blocks = blocks;
    }

    public TimeframeJava getTimeframeForScreen(int screen) {
        return timeframes.get(screen);
    }

    public IndicatorJava[] getIndicatorsForScreen(int screen) {
        return indicators.get(screen);
    }

    public void updateTimeframeForScreen(int screen, TimeframeJava timeframe) {
        timeframes.put(screen, timeframe);
    }

    public void updateIndicatorsForScreen(int screen, IndicatorJava[] indicators) {
        this.indicators.put(screen, indicators);
    }

    public BiFunction<Screens, List<TaskBlock>, BlockResultJava> getFunction() {
        return this.function;
    }

    public TimeframeIndicatorsJava getTimeframeIndicators(int screen) {
        return new TimeframeIndicatorsJava(
                timeframes.get(screen),
                indicators.get(screen)
        );
    }

}
