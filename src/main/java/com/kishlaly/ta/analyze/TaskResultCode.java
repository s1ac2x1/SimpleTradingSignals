package com.kishlaly.ta.analyze;

/**
 * присваивается последней котировке (у правого конца графика) после прогона через стратегию
 */
public enum TaskResultCode {

    SIGNAL,
    NO_DATA_QUOTES,
    NO_DATA_INDICATORS,
    NO_DATA_MACD,
    SIX_MONTHS_LOW_IS_FAR,
    NO_UPTREND,
    NO_DOWNTREND,
    UPTREND_FAILING,
    DOWNTREND_FAILING,
    HISTOGRAM_NOT_BELOW_ZERO,
    HISTOGRAM_NOT_ABOVE_ZERO,
    HISTOGRAM_NOT_ASCENDING,
    HISTOGRAM_NOT_DESCENDING,
    STOCH_NOT_ASCENDING,
    STOCH_NOT_DESCENDING,
    STOCH_NOT_ASCENDING_FROM_OVERSOLD,
    STOCH_WAS_NOT_OVERSOLD_RECENTLY,
    STOCH_NOT_DESCENDING_FROM_OVERBOUGHT,
    QUOTE_HIGH_NOT_GROWING,
    QUOTES_BELOW_EMA,
    QUOTES_ABOVE_EMA,
    QUOTE_HIGH_GROWING,
    QUOTE_HIGH_LOWING,
    QUOTE_CLOSE_NOT_GROWING,
    QUOTE_CLOSE_GROWING,
    QUOTE_CLOSE_LOWING,
    CROSSING_RULE_VIOLATED,
    CROSSING_RULE_PASSED,
    LAST_BAR_ABOVE,
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
    QUOTE_CLOSED_ABOVE_KELTNER_RULE,
    QUOTE_LOW_NOT_BELOW_EMA,
    DIVERGENCE_NOT_REACHED_PERCENTAGE
}
