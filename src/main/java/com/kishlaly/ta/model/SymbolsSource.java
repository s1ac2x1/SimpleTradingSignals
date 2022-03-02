package com.kishlaly.ta.model;

public enum SymbolsSource {
    SP500("symbols/sp500.txt"),
    SCREENER_FILTERED("symbols/screener_filtered.txt"),
    SCREENER_MANY("symbols/screener_many.txt"),
    TEST("symbols/test.txt"),
    NAGA("symbols/naga.txt");

    private String filename;

    SymbolsSource(final String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return this.filename;
    }
}
