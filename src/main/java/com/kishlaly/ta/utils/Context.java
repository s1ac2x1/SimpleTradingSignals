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

    public enum ApiSource {
        RAPID, ALPHAVANTAGE, MARKETSTACK
    }

    public static String outputFolder = "/Users/volodymyr/Investment/TA";
    public static ApiSource api = ApiSource.ALPHAVANTAGE;
    public static double parallelRequests = 10;
    public static double limitPerMinute = 75;
    public static Timeframe timeframe = Timeframe.DAY; // по умолчению для тестов
    public static Timeframe aggregationTimeframe = Timeframe.DAY; // на основе этого таймфрема агрегируются другие котировки
    public static SymbolsSource[] source = {SymbolsSource.SP500, SymbolsSource.NAGA, SymbolsSource.SCREENER_FILTERED};
    public static List<String> testOnly = new ArrayList<>();
    public static Set<String> symbols;
    public static int yearsToAnalyze = 5;

    // чтобы конвертировать в мою таймзону и сравнивать с графиками TradingView
    public static String myTimezone = "Europe/Berlin";

    // время работы NYSE и NASDAQ шесть с половиной часов или 390 минут
    // используется для дневных баров
    //public static int workingTime = 390;
    public static int workingTime = 1440; // 1440 минут в сутках

    // если меньше, то пропускать эту акцию
    public static int minimumWeekBarsCount = 50;
    public static int minimumDayBarsCount = 100;
    public static int minimumHourBarsCount = 300;

    // для логов
    public static Timeframe logTimeframe1;
    public static Timeframe logTimeframe2;
    public static Timeframe runGroups;

    // для тестирования на истоирческих данных
    public static boolean testMode;
    public static double lots = 100;
    public static StopLossStrategy stopLossStrategy = new StopLossFixedPrice(0.27);
    public static TakeProfitStrategy takeProfitStrategy = new TakeProfitFixedKeltnerTop(80);
    public static boolean massTesting;
    public static List<TakeProfitStrategy> takeProfitStrategies;

    // разное
    public static boolean trendCheckIncludeHistogram = true;
    public static boolean TRIM_DATA = true;

}
