package com.kishlaly.ta.utils;

import com.kishlaly.ta.analyze.testing.StopLossStrategy;
import com.kishlaly.ta.model.Timeframe;

import static com.kishlaly.ta.analyze.testing.StopLossStrategy.FIXED;

public class Context {

    public enum ApiSource {
        RAPID, ALPHAVANTAGE, MARKETSTACK
    }

    public static String outputFolder = "/Users/volodymyr/Investment/TA";
    public static ApiSource api;
    public static double parallelRequests = 10;
    public static double limitPerMinute = 75;
    public static Timeframe timeframe = Timeframe.DAY; // по умолчению для тестов
    public static Timeframe aggregationTimeframe = Timeframe.DAY; // на основе этого таймфрема агрегируются другие котировки
    public static String source;
    public static String singleSymbol;

    // чтобы конвертировать в мою таймзону и сравнивать с графиками TradingView
    public static String myTimezone = "Europe/Berlin";

    // время работы NYSE и NASDAQ шесть с половиной часов или 390 минут
    // используется для дневных баров
    //public static int workingTime = 390;
    public static int workingTime = 1440; // 1440 минут в сутках

    public static int minimumBarsCount = 100; // если меньше, то пропускать эту акцию

    // для логов
    public static Timeframe logTimeframe1;
    public static Timeframe logTimeframe2;

    // для тестирования на истоирческих данных
    public static double lots = 100;
    public static StopLossStrategy stopLossStrategy = FIXED;

}
