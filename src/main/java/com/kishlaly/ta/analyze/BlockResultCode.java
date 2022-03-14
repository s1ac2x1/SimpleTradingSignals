package com.kishlaly.ta.analyze;

public enum BlockResultCode {

    OK,
    NO_DATA_QUOTES,
    NO_DATA_INDICATORS,
    NO_DATA_MACD,
    N_MONTHS_LOW_IS_TOO_FAR,
    N_MONTHS_LOW_IS_TOO_CLOSE,
    NO_UPTREND_SCREEN_1,
    NO_DOWNTREND_SCREEN_1,
    UPTREND_FAILING,
    DOWNTREND_FAILING,
    HISTOGRAM_NOT_BELOW_ZERO_SCREEN_2,
    HISTOGRAM_NOT_ABOVE_ZERO_SCREEN_2,
    HISTOGRAM_NOT_ASCENDING_SCREEN_2,
    HISTOGRAM_NOT_DESCENDING_SCREEN_2,
    STOCH_NOT_ASCENDING_SCREEN_2,
    STOCH_NOT_DESCENDING_SCREEN_2,
    STOCH_NOT_ASCENDING_FROM_OVERSOLD_SCREEN_2,
    STOCH_WAS_NOT_OVERSOLD_RECENTLY_SCREEN_2,
    STOCH_NOT_DESCENDING_FROM_OVERBOUGHT_SCREEN_2,
    QUOTE_HIGH_NOT_GROWING_SCREEN_2,
    QUOTES_BELOW_EMA_SCREEN_2,
    QUOTES_NOT_BELOW_EMA_SCREEN_2,
    QUOTES_ABOVE_EMA_SCREEN_2,
    QUOTE_HIGH_GROWING_SCREEN_2,
    QUOTE_HIGH_LOWING,
    QUOTE_CLOSE_NOT_GROWING_SCREEN_2,
    QUOTE_CLOSE_GROWING_SCREEN_2,
    QUOTE_CLOSE_LOWING,
    CROSSING_RULE_VIOLATED_SCREEN_2,
    CROSSING_RULE_PASSED_SCREEN_2,
    LAST_BAR_ABOVE_SCREEN_2,
    LAST_BAR_BELOW,
    QUOTE_LOW_NOT_LOWING,
    QUOTE_CLOSE_NOT_LOWING,
    LAST_HISTOGRAM_ABOVE_ZERO,
    BEARISH_BACKBONE_NOT_CRACKED,
    HISTOGRAM_MULTIPLE_POSITIVE_ISLANDS,
    HISTOGRAM_ISLANDS_HIGHER_PRICE,
    HISTOGRAM_SECOND_BOTTOM_RATIO,
    HISTOGRAM_LAST_BAR_NOT_LOWER,
    NEGATIVE_HISTOGRAMS_LIMIT,
    DIVERGENCE_FAIL_AT_ZERO,
    DIVERGENCE_FAIL_AT_TOP,
    PRE_LAST_QUOTE_NOT_BELOW_EMA,
    LAST_QUOTE_FAILED_OPEN_BELOW_AND_CLOSE_ABOVE_EMA,
    QUOTE_CLOSED_ABOVE_KELTNER_RULE_SCREEN_2,
    QUOTE_LOW_NOT_BELOW_EMA,
    DIVERGENCE_NOT_REACHED_PERCENTAGE,
    QUOTE_NOT_GREEN,
    LAST_QUOTES_NOT_GREEN_SCREEN_2,
    LAST_QUOTES_NOT_ASCENDING_SCREEN_2,
    QUOTES_NOT_ASCENDING_AFTER_MIN,
    LAST_QUOTE_NOT_CROSSING_EMA,
    LAST_QUOTE_NOT_GREEN_SCREEN_1,
    LAST_QUOTE_ABOVE_EMA_SCREEN_2
}
