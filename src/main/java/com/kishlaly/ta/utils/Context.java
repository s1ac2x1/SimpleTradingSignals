package com.kishlaly.ta.utils;

import com.kishlaly.ta.model.Timeframe;

public class Context {

    public enum ApiSource {
        RAPID, ALPHAVANTAGE, MARKETSTACK
    }

    public static String outputFolder = "/Users/volodymyr/Investment/TA";
    public static ApiSource api;
    public static double parallelRequests = 10;
    public static double limitPerMinute = 75;
    public static Timeframe timeframe;
    public static String source;
    public static String singleSymbol;

    // чтобы конвертировать в мою таймзону и сравнивать с графиками TradingView
    public static String myTimezone = "Europe/Berlin";

    // время работы NYSE и NASDAQ шесть с половиной часов или 390 минут
    // используется для дневных баров
    public static int workingTime = 390;

    public static int minimumBarsCount = 100; // если меньше, то пропускать эту акцию

    // для логов
    public static Timeframe logTimeframe1;
    public static Timeframe logTimeframe2;

    // для тестирования на истоирческих данных
    public static String day; // вида YYYY-mm-dd

}
