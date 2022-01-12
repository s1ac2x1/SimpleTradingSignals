package com.kishlaly.ta.analyze;

import com.kishlaly.ta.analyze.tasks.Divergencies;
import com.kishlaly.ta.analyze.tasks.ThreeDisplays;
import com.kishlaly.ta.model.TaskResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.Timeframe;
import com.kishlaly.ta.model.TimeframeIndicators;
import com.kishlaly.ta.model.indicators.Indicator;

import java.util.HashMap;
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
                put(2, new Indicator[]{MACD});
            }},
            Divergencies::isBullish
    ),
    THREE_DISPLAYS_BUY(
            new HashMap<Integer, Timeframe>() {{
                put(1, WEEK);
                put(2, DAY);
            }},
            new HashMap<Integer, Indicator[]>() {{
                put(1, new Indicator[]{EMA26, MACD});
                put(2, new Indicator[]{EMA13, MACD, STOCH});
            }},
            ThreeDisplays::buySignal
    ),
    THREE_DISPLAYS_BUY_TYPE2(
            new HashMap<Integer, Timeframe>() {{
                put(1, WEEK);
                put(2, DAY);
            }},
            new HashMap<Integer, Indicator[]>() {{
                put(1, new Indicator[]{EMA26, MACD});
                put(2, new Indicator[]{EMA13, MACD, STOCH});
            }},
            ThreeDisplays::buySignalType2
    ),
    THREE_DISPLAYS_SELL(
            new HashMap<Integer, Timeframe>() {{
                put(1, WEEK);
                put(2, DAY);
            }},
            new HashMap<Integer, Indicator[]>() {{
                put(1, new Indicator[]{EMA26, MACD});
                put(2, new Indicator[]{EMA13, MACD, STOCH});
            }},
            ThreeDisplays::sellSignal
    );

    TaskType(final Map<Integer, Timeframe> timeframes,
             final Map<Integer, Indicator[]> indicators,
             BiFunction<SymbolData, SymbolData, TaskResult> function) {
        this.timeframes = timeframes;
        this.indicators = indicators;
        this.function = function;
    }

    private Map<Integer, Timeframe> timeframes;
    private Map<Integer, Indicator[]> indicators;
    private BiFunction<SymbolData, SymbolData, TaskResult> function;
    private TimeframeIndicators timeframeIndicators;

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

    public BiFunction<SymbolData, SymbolData, TaskResult> getFunction() {
        return this.function;
    }

    public TimeframeIndicators getTimeframeIndicators(int screen) {
        return new TimeframeIndicators(
                timeframes.get(screen),
                indicators.get(screen)
        );
    }

}
