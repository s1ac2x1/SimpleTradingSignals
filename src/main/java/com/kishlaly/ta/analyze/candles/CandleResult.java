package com.kishlaly.ta.analyze.candles;

public enum CandleResult {

    HANGING_MAN_BASIC("Classic model"),
    HANGING_MAN_NEXT_QUOTE_RED("The next candle is red"),
    HANGING_MAN_NEXT_GAPPED_DOWN("The next candle opened below the body"),
    NO_RESULT("");

    private String comments;

    CandleResult(final String comments) {
        this.comments = comments;
    }

    public String getComments() {
        return this.comments;
    }
}
