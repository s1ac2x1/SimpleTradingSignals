package com.kishlaly.ta;

import com.kishlaly.ta.analyze.tasks.blocks.groups.ThreeDisplays_Buy_3Java;
import com.kishlaly.ta.analyze.tasks.blocks.groups.ThreeDisplays_Buy_8Java;
import com.kishlaly.ta.model.SymbolsSourceJava;
import com.kishlaly.ta.model.TimeframeJava;
import com.kishlaly.ta.utils.ContextJava;
import com.kishlaly.ta.utils.RunUtilsJava;

import static com.kishlaly.ta.cache.CacheReaderJava.getSymbols;
import static com.kishlaly.ta.utils.RunUtilsJava.singleSymbol;

/**
 * @author Vladimir Kishlaly
 * @since 15.11.2021
 */
public class MainJava {

    public static void main(String[] args) throws Exception {
        ContextJava.aggregationTimeframe = TimeframeJava.DAY;
        ContextJava.source = new SymbolsSourceJava[]{
                SymbolsSourceJava.SP500
        };
        singleSymbol("AAPL"); // for single test
        ContextJava.symbols = getSymbols();
        //buildCache(Context.basicTimeframes, false);

        //RunUtilsJava.testOneStrategy_(new ThreeDisplays_Buy_8Java());
        RunUtilsJava.buildTasksAndStrategiesSummary_();
        //RunUtilsJava.testStrategiesOnSpecificDate_("15.03.2022");

    }
}

/*
TODO

проверить все стратегии снова, потому что в процессе рефакторинга я находил ошибки в блоках

try to expand Long_ScreenOne_SoftTrendCheck and require the last two green bars
take the first screen check from type_4 into a separate subgroup and test it on other strategies

new strategy
1 screen: the last two MACD histograms are below zero
          last one is rising
2 screen: last bar is green

new strategy
1 screen: none
2 screen: three last quotes green
          three last quotes are rising (high & low)
          the last quote crosses EMA13

new strategy
1 screen: two quotes are green and quote.high are rising
          prelast is below EMA26
2 screen: last quote is green

new strategy (inspired by [D] TWH 15.03.2022)
1 screen: none
2 screen:
         the prelast quote is very close to the bottom Bollinger band
         the prelast quote is green
         Last quote is green
         MACD histogram is negative and growing
         Stochastic %D is rising
         prelast %D stochastic is below 10
SL at 2xATR
TP at 70-80% of Keltner channel

what if we buy only at the border of the channels? are there already such strategies?

Finish the EFI strategies
also add in other strategies step "filter entry point if EFI is below zero" and test them
finish other strategies marked todo
What did Elder write about chart analysis and price deviations?
EFI divergence

Find how to download the chart for gold (ticker GOLD?) and analyze the price touching the Bollinger Bands:
    touching the lower band - long position with TP just below the middle band
    touching the upper band - short position with TP just above the average band
    Questions: should I check the long-term trend? What frames should I use - 2 hours and 25 minutes?

it would be great to test the Cartesian product of all groups
 
 */