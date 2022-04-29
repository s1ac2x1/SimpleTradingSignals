package com.kishlaly.ta;

import com.kishlaly.ta.analyze.tasks.ThreeDisplays;
import com.kishlaly.ta.model.SymbolsSource;
import com.kishlaly.ta.model.Timeframe;
import com.kishlaly.ta.utils.Context;

import java.util.ArrayList;

import static com.kishlaly.ta.cache.CacheBuilder.buildCache;
import static com.kishlaly.ta.cache.CacheReader.getSymbols;
import static com.kishlaly.ta.utils.RunUtils.*;

/**
 * @author Vladimir Kishlaly
 * @since 15.11.2021
 */
public class Main {

    public static void main(String[] args) throws Exception {

        Context.aggregationTimeframe = Timeframe.DAY;

        Context.source = new SymbolsSource[]{
                //SymbolsSource.SP500,
                SymbolsSource.SP500_RANDOM,
                //SymbolsSource.NAGA,
                //SymbolsSource.SCREENER_FILTERED
                //SymbolsSource.SCREENER_MANY_P_1,
                //SymbolsSource.SCREENER_MANY_P_2,
                //SymbolsSource.SCREENER_MANY_P_3,
                //SymbolsSource.SCREENER_MANY_RANDOM
                //SymbolsSource.TEST
        };

//        Context.testOnly = new ArrayList<String>() {{
//            add("AAPL");
//        }};

        Context.symbols = getSymbols();
        Context.yearsToAnalyze = 5;
        //Context.trimToDate = "15.03.2022";

        //buildCache(new Timeframe[][]{{Timeframe.WEEK, Timeframe.DAY}}, false);

        ThreeDisplays.Config.FILTER_BY_KELTNER_ENABLED = true;
        ThreeDisplays.Config.FILTER_BY_KELTNER = 20;

        //buyDaily();
        //testStrategiesOnSpecificDate_("15.03.2022");
        //testOneStrategy_();
        buildTasksAndStrategiesSummary_();

        // Проверить Long_ScreenOne_SoftTrendCheck у всех стратегий, включающих первый экран
        //попробовать расширить Long_ScreenOne_SoftTrendCheck и требовать ДВА послдених зеленых столбика


//        закончить стратегии по EFI
//           так же добавить в другие стратегии шаг "фильтровать точку входа, если EFI ниже нуля и проверить
//        закончить другие стратегии, помеченные todo
//        что там Элдер писал про анализ графиков и отклонения цен?
//        дивергенции EFI

// найти как скачать график по золоту (тикер GOLD?) и проанализвать касание ценой лент Боллинжера:
//    касание нижней ленты - длинная позиция с TP чуть ниже среднейл ленты
//    касание верхней ленты - короткая позиция с TP чуть выше средней
// вопросы: нужно проверять долгосрочный тренд? какоие фреймы использовать - 2часа и 25минут?
// протестировать все

// было бы здорово протестировать декартово произведение всех групп


    }
}