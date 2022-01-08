package com.kishlaly.ta.model;

import java.util.ArrayList;
import java.util.List;

public class HistoricalTesting {

    private SymbolData data;
    private List<Quote> signals = new ArrayList<>();

    public HistoricalTesting(final SymbolData data, final List<Quote> signals) {
        this.data = data;
        this.signals = signals;
    }

    public SymbolData getData() {
        return this.data;
    }

    public void setData(final SymbolData data) {
        this.data = data;
    }

    public List<Quote> getSignals() {
        return this.signals;
    }

    public void setSignals(final List<Quote> signals) {
        this.signals = signals;
    }
}
