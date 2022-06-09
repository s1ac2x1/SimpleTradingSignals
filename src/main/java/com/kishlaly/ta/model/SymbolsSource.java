package com.kishlaly.ta.model;

import com.kishlaly.ta.utils.Context;

public enum SymbolsSource {
    SP500("symbols" + Context.fileSeparator + "sp500.txt", false),
    SP500_RANDOM("symbols" + Context.fileSeparator + "sp500.txt", true),

    SCREENER_FILTERED("symbols" + Context.fileSeparator + "screener_filtered.txt", false),
    SCREENER_FILTERED_RANDOM("symbols" + Context.fileSeparator + "screener_filtered.txt", true),

    SCREENER_MANY_P_1("symbols" + Context.fileSeparator + "screener_many_p_1.txt", false),
    SCREENER_MANY_P_2("symbols" + Context.fileSeparator + "screener_many_p_2.txt", false),
    SCREENER_MANY_P_3("symbols" + Context.fileSeparator + "screener_many_p_3.txt", false),
    SCREENER_MANY_RANDOM("symbols" + Context.fileSeparator + "screener_many.txt", true),

    TEST("symbols" + Context.fileSeparator + "test.txt", false),

    NAGA("symbols" + Context.fileSeparator + "naga.txt", false);

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
