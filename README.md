This project manifested itself as an auxiliary utility for analyzing historical stock price data. For a quick start I decided to use the excellent library for technical analysis https://github.com/ta4j/ta4j.

After a while I found out that my project can be used for searching behavioral patterns of price charts, and now I am adding new strategies in my spare time.

The main inspirations: Alexander Elder and Thomas Bulkowski

From Mr. Elder's books I got great ideas about how to use several timeframes, and Mr. Bulkowski's books, as I think, are just created to be programmed into automatic scanners.

Data source provider used inside is https://www.alphavantage.co/
This is a paid service, however, I've included historical quotes for SP500 list in `data/cache` folder.

Key points:
* build cache
* analyze using various strategies
* the outup will be in `data/signal` folder
* debug information stored in `data/debug` folder
* `RunUtils` has a lot of useful methods, among them are:
  * `testOneStrategy` for single test
  * `testStrategiesOnSpecificDate_` for capturing the output of the all strategies on specific date. Useful when you noticed a good potential entrypoint on the chart and want to see which strategies are able to give a singal
  * `buildTasksAndStrategiesSummary_` is extremely useful to compare which strategy gives more return, more TP and less SL trades

The naming conventions depict the purpose, eg: `Long_ScreenOne_EMA_LastBarCrosses` says it will check whether the last quote has crossed EMA on a long-term timeframe (aka screen-1) in order to open long position.

Package `com.kishlaly.ta.analyze.tasks.blocks.groups` contains the list of all strategies. They have been build in a modular way, it's faster and more convenient to create new ones.

For example, one of my most favourite strategies `ThreeDisplays_Buy_2` has the following blocks:
* validation
* "soft" uptrend check: the last EMA26 is rising and the last quote is green
* MACD histogram is below zero and rising on the last two values
* %D or %K values of Stochastic were oversold on the last 5 values
* last value of %D of Stochastic is higher than the previous
* last two quotes are ascending and one of them have crossed EMA-15 in a correct way
* checks if it's already too late and price has jumped above the limit (in this case, above 20% from middle to top of Keltner channel indicator)

and so on and so forth :)

#### Example of testing single strategy
Let's see how much we could earn during the last 5 years on stock `TER` with `ThreeDisplays_Buy_2`:
```java
    public static void main(String[] args) throws Exception {
        Context.aggregationTimeframe = Timeframe.DAY;
        Context.source = new SymbolsSource[]{
                SymbolsSource.SP500
        };
        singleSymbol("TER");
        Context.symbols = getSymbols();
        buildCache(Context.basicTimeframes, false);
        RunUtils.testOneStrategy_(new ThreeDisplays_Buy_2());
    }
```

the output in `tests/single.txt` says:

```text
    [WEEK][DAY] TER - THREE_DISPLAYS_BUY - ThreeDisplays_Buy_2
	trendCheckIncludeHistogram = true
	each trade size = $10000.0
	SL [Fixed] price 0.27
	TP [Fixed] Keltner 80% top 
	TP/SL = 28/5 = 84.85% / 15.16%%
	Total profit after 1% commissions per trade = 8703.6
	Total profit / loss = 13216.56 / -4512.96
	min duration = 1 days
	max duration = 26 days [22 JULY 2021 - 17 AUGUST 2021]
	avg duration = 10 days
	min profit = 2.71% [23 MAY 2017 - 31 MAY 2017]
	max profit = 12.47% [15 MARCH 2021 - 5 APRIL 2021]
	min loss = -5.5% [24 AUGUST 2018 - 11 SEPTEMBER 2018]
	max loss = -10.42% [4 FEBRUARY 2020 - 24 FEBRUARY 2020]
	avg profit / loss = 571.66 / -803.19
```

Well, 28 profitable positions out of 5 losses. The total net profit is $8703.6 (depositary and other broker's commisions are not included, just 1% per each trade like in InteractiveBrokers).

That output also has logs for all quotes: whether it was selected as signal or why not.

#### Comparing different strategies
There is an option how to compare the performance of several strategies by using `RunUtils.buildTasksAndStrategiesSummary_()`

In the following example it takes the random 30 stocks out of SP500 list and runs serveral selected stratgies over them:
```java
    public static void main(String[] args) throws Exception {
        Context.aggregationTimeframe = Timeframe.DAY;
        Context.source = new SymbolsSource[]{
                SymbolsSource.SP500_RANDOM // to use random stocks out of the list you have to build the full cache first
        };
        Context.symbols = getSymbols();
        // assuming the cache is ready
        RunUtils.buildTasksAndStrategiesSummary_();
    }
```

the output from `tests/summary.txt`
```text
ThreeDisplays_Buy_7: 68383.55
ThreeDisplays_Buy_6: 62437.28
ThreeDisplays_Buy_4: 51500.21
ThreeDisplays_Buy_3: 23098.14
ThreeDisplays_Buy_1: 16004.71
ThreeDisplays_Buy_2: 14097.22
ThreeDisplays_Buy_5: 12968.72

ThreeDisplays_Buy_4: TP/SL = 218/31
ThreeDisplays_Buy_5: TP/SL = 112/32
ThreeDisplays_Buy_6: TP/SL = 221/103
ThreeDisplays_Buy_7: TP/SL = 632/237
ThreeDisplays_Buy_2: TP/SL = 572/246
ThreeDisplays_Buy_3: TP/SL = 82/47
ThreeDisplays_Buy_1: TP/SL = 448/247
```

it means `ThreeDisplays_Buy_4` is the winner as it gave 218 profitable positions and just 31 were closed by StopLoss over the last 5 years (can be extended) tested on 30 random stocks. 

In terms of profit, the strategy `ThreeDisplays_Buy_7` has won, but look how many signals it produced: 632 + 237. The TP/SL ratio is still very good, but this strategy suits for more aggressive trading.

Please note, that your can select which TP or SL strategy to use. By deafult I use:
* fixed `StopLossFixedPrice(0.27)` which finds the minimal quote from the last 20 and sets SL 27 cent below
* fixed `TakeProfitFixedKeltnerTop(70)` which stops position when the next quote reached 70% of the height between average and top of Keltner channel indicator

There are other TP/SL strategies, including volatile, that will follow the price based on some rules (but never set the SL lower or course). Pls find them in `com.kishlaly.ta.analyze.testing.tp` and `com.kishlaly.ta.analyze.testing.ls`

`RunUtils.buildTasksAndStrategiesSummary_()` also produced a more detailed report located at `data/stats` with output like
```text
signal 2019-03-11T09:30-04:00[US/Eastern]
	SL: 53.95
	TP: 57.61
	open price: 56.33
	cloce price: 57.61
	closed: 2019-03-13T09:30-04:00[US/Eastern]
	profitable: true
	gap up: false
	gap down: false


signal 2018-09-27T09:30-04:00[US/Eastern]
	SL: 62.57
	TP: 65.65
	open price: 64.28
	cloce price: 62.57
	closed: 2018-10-11T09:30-04:00[US/Eastern]
	profitable: false
	gap up: false
	gap down: false
```