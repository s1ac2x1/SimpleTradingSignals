package com.kishlaly.ta.utils;

import com.kishlaly.ta.analyze.testing.sl.StopLossFixedPriceJava;
import com.kishlaly.ta.analyze.testing.sl.StopLossStrategyJava;
import com.kishlaly.ta.analyze.testing.tp.TakeProfitFixedKeltnerTopJava;
import com.kishlaly.ta.analyze.testing.tp.TakeProfitStrategyJava;
import com.kishlaly.ta.model.SymbolsSourceJava;
import com.kishlaly.ta.model.TimeframeJava;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ContextJava {

    public static double accountBalance = 10_000; // maximum position size
    public static int tradeCommission = 1; // %

    public enum ApiSource {
        RAPID, ALPHAVANTAGE, MARKETSTACK
    }

    public static TimeframeJava[][] basicTimeframes = {{TimeframeJava.WEEK, TimeframeJava.DAY}};
    public static String outputFolder = "data";
    public static ApiSource api = ApiSource.ALPHAVANTAGE;
    public static double parallelRequests = 10;
    public static double limitPerMinute = 75;
    public static TimeframeJava timeframe = TimeframeJava.DAY;
    // main aggregation timeframe. Weekly quotes will be calculated from it
    public static TimeframeJava aggregationTimeframe = TimeframeJava.DAY;
    public static SymbolsSourceJava[] source = {SymbolsSourceJava.SP500};
    public static List<String> testOnly = new ArrayList<>();
    public static Set<String> symbols;
    public static int yearsToAnalyze = 5;
    public static boolean lowPricesOnly;
    public static int lowPriceLimit = 20;

    // to convert to my timezone and compare with TradingView charts
    public static String myTimezone = "Europe/Berlin";

    public static int workingTime = 1440; // 1440 minutes in a day

    // If less, then skip this stock
    public static int minimumWeekBarsCount = 50;
    public static int minimumDayBarsCount = 100;
    public static int minimumHourBarsCount = 300;

    // for logs
    public static TimeframeJava logTimeframe1;
    public static TimeframeJava logTimeframe2;
    public static TimeframeJava runGroups;

    // for testing on historical data
    public static boolean testMode;
    public static StopLossStrategyJava stopLossStrategy = new StopLossFixedPriceJava(0.27);
    public static TakeProfitStrategyJava takeProfitStrategy = new TakeProfitFixedKeltnerTopJava(80);
    public static boolean massTesting;
    public static List<TakeProfitStrategyJava> takeProfitStrategies;

    // misc
    public static boolean trendCheckIncludeHistogram = true;
    public static boolean TRIM_DATA = true;
    public static String trimToDate;
    public static String fileSeparator = System.getProperty("file.separator");
    public static final String TESTS_FOLDER = "tests";
    public static final String SINGLE_TXT = "single_java.txt";
    public static final String MASS_TXT = "mass.txt";
    public static final int MIN_POSSIBLE_QUOTES = 11;

}
