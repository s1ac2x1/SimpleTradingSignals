package com.kishlaly.ta.analyze.candles;

public enum CandleResult {

    HANGING_MAN_BASIC("Классическая модель"),
    HANGING_MAN_NEXT_QUOTE_RED("Следующая котировка красная"),
    HANGING_MAN_NEXT_GAPPED_DOWN("Следующая котировка открылась ниже тела"),
    NO_RESULT("");

    private String comments;

    CandleResult(final String comments) {
        this.comments = comments;
    }

    public String getComments() {
        return this.comments;
    }
}
