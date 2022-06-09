package com.kishlaly.ta.model;

public enum SymbolsSource {
    SP500("symbols" + System.getProperty("file.separator") + "sp500.txt", false),
    SP500_RANDOM("symbols" + System.getProperty("file.separator") + "sp500.txt", true),

    SCREENER_FILTERED("symbols" + System.getProperty("file.separator") + "screener_filtered.txt", false),
    SCREENER_FILTERED_RANDOM("symbols" + System.getProperty("file.separator") + "screener_filtered.txt", true),

    SCREENER_MANY_P_1("symbols" + System.getProperty("file.separator") + "screener_many_p_1.txt", false),
    SCREENER_MANY_P_2("symbols" + System.getProperty("file.separator") + "screener_many_p_2.txt", false),
    SCREENER_MANY_P_3("symbols" + System.getProperty("file.separator") + "screener_many_p_3.txt", false),
    SCREENER_MANY_RANDOM("symbols" + System.getProperty("file.separator") + "screener_many.txt", true),

    TEST("symbols" + System.getProperty("file.separator") + "test.txt", false),

    NAGA("symbols" + System.getProperty("file.separator") + "naga.txt", false);

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
