package com.kishlaly.ta.model;

import com.kishlaly.ta.model.indicators.Indicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolData {

    public String symbol;
    public Timeframe timeframe;
    public List<Quote> quotes;
    public Map<Indicator, List<? extends EntityWithDate>> indicators = new HashMap<>();

    public Quote getLastQuote() {
        return quotes.get(quotes.size() - 1);
    }

    public Quote getPreLastQuote() {
        return quotes.get(quotes.size() - 2);
    }

    public SymbolData copy() {
        SymbolData copy = new SymbolData();
        copy.symbol = symbol;
        copy.timeframe = timeframe;
        copy.quotes = new ArrayList<>(quotes);
        copy.indicators = new HashMap<>(indicators);
        return copy;
    }

    public void clear() {
        quotes.clear();
        indicators.clear();
    }

}
