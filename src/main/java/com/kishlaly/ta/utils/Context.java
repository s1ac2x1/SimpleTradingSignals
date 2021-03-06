package com.kishlaly.ta.utils;

import com.kishlaly.ta.analyze.testing.sl.StopLossFixedPrice;
import com.kishlaly.ta.analyze.testing.sl.StopLossStrategy;
import com.kishlaly.ta.analyze.testing.tp.TakeProfitFixedKeltnerTop;
import com.kishlaly.ta.analyze.testing.tp.TakeProfitStrategy;
import com.kishlaly.ta.model.SymbolsSource;
import com.kishlaly.ta.model.Timeframe;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Context {

    public static double accountBalance = 10_000; // maximum position size
    public static int tradeCommission = 1; // %

    public enum ApiSource {
        RAPID, ALPHAVANTAGE, MARKETSTACK
    }

    public static Timeframe[][] basicTimeframes = {{Timeframe.WEEK, Timeframe.DAY}};
    public static String outputFolder = "data";
    public static ApiSource api = ApiSource.ALPHAVANTAGE;
    public static double parallelRequests = 10;
    public static double limitPerMinute = 75;
    public static Timeframe timeframe = Timeframe.DAY;
    // main aggregation timeframe. Weekly quotes will be calculated from it
    public static Timeframe aggregationTimeframe = Timeframe.DAY;
    public static SymbolsSource[] source = {SymbolsSource.SP500};
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
    public static Timeframe logTimeframe1;
    public static Timeframe logTimeframe2;
    public static Timeframe runGroups;

    // for testing on historical data
    public static boolean testMode;
    public static StopLossStrategy stopLossStrategy = new StopLossFixedPrice(0.27);
    public static TakeProfitStrategy takeProfitStrategy = new TakeProfitFixedKeltnerTop(80);
    public static boolean massTesting;
    public static List<TakeProfitStrategy> takeProfitStrategies;

    // misc
    public static boolean trendCheckIncludeHistogram = true;
    public static boolean TRIM_DATA = true;
    public static String trimToDate;
    public static String fileSeparator = System.getProperty("file.separator");
    public static final String TESTS_FOLDER = "tests";
    public static final String SINGLE_TXT = "single.txt";
    public static final String MASS_TXT = "mass.txt";
    public static final int MIN_POSSIBLE_QUOTES = 11;

}
