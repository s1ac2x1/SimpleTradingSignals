package com.kishlaly.ta.analyze;

import com.kishlaly.ta.analyze.tasks.AbstractTaskJava;
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlockJava;
import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.ScreensJava;
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

public enum TaskTypeJava {
    MACD_BULLISH_DIVERGENCE(
            new HashMap<Integer, TimeframeJava>() {{
                put(1, WEEK);
                put(2, DAY);
            }},
            new HashMap<Integer, IndicatorJava[]>() {{
                put(1, new IndicatorJava[]{EMA26, MACD});
                put(2, new IndicatorJava[]{MACD, KELTNER});
            }},
            AbstractTaskJava::check
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
            AbstractTaskJava::check
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
            AbstractTaskJava::check
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
            AbstractTaskJava::check
    );

    TaskTypeJava(final Map<Integer, TimeframeJava> timeframes,
                 final Map<Integer, IndicatorJava[]> indicators,
                 BiFunction<ScreensJava, List<TaskBlockJava>, BlockResultJava> function) {
        this.timeframes = timeframes;
        this.indicators = indicators;
        this.function = function;
    }

    private Map<Integer, TimeframeJava> timeframes;
    private Map<Integer, IndicatorJava[]> indicators;
    private BiFunction<ScreensJava, List<TaskBlockJava>, BlockResultJava> function;
    private TimeframeIndicatorsJava timeframeIndicators;
    private List<TaskBlockJava> blocks = new ArrayList<>();

    public List<TaskBlockJava> getBlocks() {
        return this.blocks;
    }

    public void setBlocks(final List<TaskBlockJava> blocks) {
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

    public BiFunction<ScreensJava, List<TaskBlockJava>, BlockResultJava> getFunction() {
        return this.function;
    }

    public TimeframeIndicatorsJava getTimeframeIndicators(int screen) {
        return new TimeframeIndicatorsJava(
                timeframes.get(screen),
                indicators.get(screen)
        );
    }

}
