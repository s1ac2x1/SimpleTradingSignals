package com.kishlaly.ta.model;

public enum SymbolsSource {
    SP500("symbols/sp500.txt", false),
    SP500_RANDOM("symbols/sp500.txt", true),

    SCREENER_FILTERED("symbols/screener_filtered.txt", false),
    SCREENER_FILTERED_RANDOM("symbols/screener_filtered.txt", true),

    SCREENER_MANY("symbols/screener_many.txt", false),
    SCREENER_MANY_RANDOM("symbols/screener_many.txt", true),

    TEST("symbols/test.txt", false),

    NAGA("symbols/naga.txt", false),
    NAGA_RANDOM("symbols/naga.txt", true);

    private String filename;
    private boolean random;

    SymbolsSource(final String filename, boolean random) {
        this.filename = filename;
        this.random = random;
    }

    public String getFilename() {
        return this.filename;
    }

    public boolean isRandom() {
        return this.random;
    }
}
