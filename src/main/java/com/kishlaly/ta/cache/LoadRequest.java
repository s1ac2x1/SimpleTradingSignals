package com.kishlaly.ta.cache;

import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.model.TimeframeJava;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadRequest {

    private TimeframeJava timeframe;
    private List<String> symbols;
    private CacheType cacheType;
    private Map<String, String> config = new HashMap<>();
    private IndicatorJava indicator;

    public LoadRequest(final CacheType cacheType, final TimeframeJava timeframe, final List<String> symbols) {
        this.timeframe = timeframe;
        this.symbols = symbols;
        this.cacheType = cacheType;
    }

    public TimeframeJava getTimeframe() {
        return this.timeframe;
    }

    public void setTimeframe(final TimeframeJava timeframe) {
        this.timeframe = timeframe;
    }

    public List<String> getSymbols() {
        return this.symbols;
    }

    public void setSymbols(final List<String> symbols) {
        this.symbols = symbols;
    }

    public CacheType getType() {
        return this.cacheType;
    }

    public void setType(final CacheType cacheType) {
        this.cacheType = cacheType;
    }

    public void setConfig(String name, String value) {
        config.put(name, value);
    }

    public String getConfig(String name) {
        return config.get(name);
    }

    public IndicatorJava getIndicator() {
        return this.indicator;
    }

    public void setIndicator(final IndicatorJava indicator) {
        this.indicator = indicator;
    }
}
