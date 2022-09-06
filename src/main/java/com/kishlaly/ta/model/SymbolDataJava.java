package com.kishlaly.ta.model;

import com.kishlaly.ta.model.indicators.IndicatorJava;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolDataJava {

    public String symbol;
    public TimeframeJava timeframe;
    public List<QuoteJava> quotes;
    public Map<IndicatorJava, List<? extends AbstractModelJava>> indicators = new HashMap<>();

    public QuoteJava getLastQuote() {
        return quotes.get(quotes.size() - 1);
    }

    public QuoteJava getPreLastQuote() {
        return quotes.get(quotes.size() - 2);
    }

    public SymbolDataJava copy() {
        SymbolDataJava copy = new SymbolDataJava();
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
