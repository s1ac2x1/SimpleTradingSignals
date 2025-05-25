package com.kishlaly.ta;

import com.kishlaly.ta.analyze.tasks.blocks.groups.ThreeDisplays_Buy_1;
import com.kishlaly.ta.analyze.testing.sl.StopLossFixedPrice;
import com.kishlaly.ta.analyze.testing.tp.TakeProfitFixedKeltnerTop;
import com.kishlaly.ta.model.SymbolsSource;
import com.kishlaly.ta.model.Timeframe;
import com.kishlaly.ta.utils.Context;

import static com.kishlaly.ta.analyze.TaskType.THREE_DISPLAYS_BUY;
import static com.kishlaly.ta.analyze.testing.TaskTester.testOneStrategy;
import static com.kishlaly.ta.cache.CacheReader.getSymbols;
import static com.kishlaly.ta.utils.RunUtils.singleSymbol;

/**
 * @author Vladimir Kishlaly
 * @since 15.11.2021
 */
public class Main {

    public static void main(String[] args) throws Exception {
        Context.aggregationTimeframe = Timeframe.DAY;
        Context.source = new SymbolsSource[]{
                SymbolsSource.SP500
        };
        singleSymbol("A"); // for single test
        Context.symbols = getSymbols();
        //buildCache(Context.basicTimeframes, false);
        testOneStrategy(
                Context.basicTimeframes,
                THREE_DISPLAYS_BUY,
                new ThreeDisplays_Buy_1(),
                new StopLossFixedPrice(0.27),
                new TakeProfitFixedKeltnerTop(80));

    }
}

/*
TODO

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