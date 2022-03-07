package com.kishlaly.ta.model;

import com.kishlaly.ta.model.indicators.Indicator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolData {

    public String symbol;
    public Timeframe timeframe;
    public List<Quote> quotes;
    public Map<Indicator, List> indicators = new HashMap<>();

    public Quote getLastQuote() {
        return quotes.get(quotes.size() - 1);
    }

}
