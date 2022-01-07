package com.kishlaly.ta.model;

import java.util.ArrayList;
import java.util.List;

public class HistoricalTesting {

    private SymbolData data;
    private List<Quote> entries = new ArrayList<>();

    public HistoricalTesting(final SymbolData data, final List<Quote> entries) {
        this.data = data;
        this.entries = entries;
    }

    public SymbolData getData() {
        return this.data;
    }

    public void setData(final SymbolData data) {
        this.data = data;
    }

    public List<Quote> getEntries() {
        return this.entries;
    }

    public void setEntries(final List<Quote> entries) {
        this.entries = entries;
    }
}
