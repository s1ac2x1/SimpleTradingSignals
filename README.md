## Disclaimer

This system of trading signals is not an inducement to action.
It is just a scanner, which reveals the patterns of behavior of historical price fluctuations, which in itself cannot
give predictions for the future.

Keep in mind that the market is not a mathematical model, but a living organism that lives primarily on the emotions of
the crowd.

Before deciding to open a position, you need to study the fundamentals of the underlying stock and many other factors.

Remember that the technical analysis only gives clues, but is by no means the only right tool.

## History

This project manifested itself as an auxiliary utility for analyzing historical stock price data. For a quick start I
decided to use the excellent library for technical analysis https://github.com/ta4j/ta4j.

After a while I found out that my project can be used for searching behavioral patterns of price charts, and now I am
adding new strategies in my spare time.

## Inspiration

The main inspirations: Alexander Elder and Thomas Bulkowski

From Mr. Elder's books I got great ideas about how to use several timeframes, and Mr. Bulkowski's books, as I think, are
just created to be programmed into automatic scanners.

* [Two Roads Diverged: Trading Divergences](https://www.amazon.de/-/en/gp/product/B006V3CWDS)
* [Entries and Exits: Visits to Sixteen Trading Rooms](https://www.amazon.de/-/en/gp/product/B000SEKFG2)
* [Step by Step Trading: The Essentials of Computerized Technical Trading](https://www.amazon.de/-/en/gp/product/B00VH7WMSI/)
* [Study Guide for Sell and Sell Short](https://www.amazon.de/-/en/gp/product/0470200472)
* [Encyclopedia of Chart Patterns](https://www.amazon.de/-/en/Thomas-N-Bulkowski/dp/1119739683)
* [Chart Patterns: After the Buy ](https://www.amazon.de/-/en/Thomas-N-Bulkowski/dp/1119274907)
* [Swing and Day Trading: Evolution of a Trader](https://www.amazon.de/-/en/Thomas-N-Bulkowski/dp/1118464222)

## Quotes provider

Data source provider used inside is https://www.alphavantage.co/
This is a paid service, however, I've included historical quotes for SP500 list in `data/cache` folder.

## Key points

### Download and cache quotes

The loading of historical data depends on the provider and the number of requests per second it allows.
These values are configured by the parameters `limitPerMinute` and `parallelRequests`

During data loading the log will display the maximum cache build time depending on the allowed API load.

Example for 75 req/min for SP500:

```text
0:7:5 left...
Loading DAY quotes...

0:6:56 left...
Loading DAY quotes...
```

### Indicators used

* Exponential Moving Average with period 13 and 26 (depending on timeframe)
* MACD (12/26/9)
* Stochastic oscillator (14/1/3)
* Keltner Channels
* Bollinger Band
* Elders Force Index
* Average True Range

### Output

* `data/signal` for signals
* `data/debug` for debug information
* `tests/single.txt` for single test
* `tests/summary.txt` for summary
* `data/stats` for another sort of detailed report

### Naming conventions

The naming conventions depict the purpose, eg: `Long_ScreenOne_EMA_LastBarCrosses` says it will check whether the last
quote has crossed EMA on a long-term timeframe (aka screen-1) in order to open long position.

### What's inside

Package `com.kishlaly.ta.analyze.tasks.blocks.groups` contains the list of all strategies. They have been build in a
modular way, it's faster and more convenient to create new ones.

For example, one of my most favourite strategies `ThreeDisplays_Buy_2` has the following blocks:

* validation
* "soft" uptrend check: the last EMA26 is rising and the last quote is green
* MACD histogram is below zero and rising on the last two values
* %D or %K values of Stochastic were oversold on the last 5 values
* last value of %D of Stochastic is higher than the previous
* last two quotes are ascending and one of them have crossed EMA-15 in a correct way
* checks if it's already too late and price has jumped above the limit (in this case, above 20% from middle to top of
  Keltner channel indicator)

`buildTasksAndStrategiesSummary_` is extremely useful to compare which strategy gives more return, more TP and less SL
trades

### Testing single strategy

Let's see how much we could earn during the last 5 years on stock `TER` with `ThreeDisplays_Buy_2`:

```java
    public static void main(String[]args)throws Exception{
        Context.aggregationTimeframe=Timeframe.DAY;
        Context.source=new SymbolsSource[]{
        SymbolsSource.SP500
        };
        singleSymbol("TER");
        Context.symbols=getSymbols();
        buildCache(Context.basicTimeframes,false);
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
	TP/SL = 28/5 = 84.85% / 15.16%
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

Well, 28 profitable positions out of 5 losses. The total net profit is $8703.6 (depositary and other broker's commisions
are not included, just 1% per each trade like in InteractiveBrokers).

That output also has logs for all quotes: whether it was selected as signal or why not:

```text
    22 JULY 2021 --- LOSS -8.42% 26 days [till 17 AUGUST 2021]
    23 JUNE 2021 --- PROFIT 4.72% 5 days [till 28 JUNE 2021]
    19 MAY 2021 --- PROFIT 7.39% 9 days [till 28 MAY 2021]
    18 MAY 2021 --- PROFIT 10.54% 10 days [till 28 MAY 2021]
    14 MAY 2021 --- PROFIT 8.19% 14 days [till 28 MAY 2021]
    7 MAY 2021 --- LOSS -9.29% (gap down) 4 days [till 11 MAY 2021]
    16 MARCH 2021 --- PROFIT 12.14% 20 days [till 5 APRIL 2021]
    15 MARCH 2021 --- PROFIT 12.47% 21 days [till 5 APRIL 2021]
    10 FEBRUARY 2021 --- PROFIT 4.31% 1 days [till 11 FEBRUARY 2021]
    9 FEBRUARY 2021 --- PROFIT 4.55% 2 days [till 11 FEBRUARY 2021]
    5 FEBRUARY 2021 --- PROFIT 6.47% 6 days [till 11 FEBRUARY 2021]
    ...
```

### Run all on specific date

Useful for capturing the output of the all strategies on specific date - when you noticed a good potential entrypoint on
the chart and want to see which strategies are able to give a singal.

Let's say by looking at the chart you've noticed a nice entry point for a long position for `AAPL` on 16th of Marh 2022:

![AAPL](https://github.com/s1ac2x1/SimpleTradingSignals/blob/master/img/AAPL_16_03_2022.png?raw=true)

Let's find out how many strategies can find it:

```java
    public static void main(String[]args)throws Exception{
        Context.aggregationTimeframe=Timeframe.DAY;
        Context.source=new SymbolsSource[]{
        SymbolsSource.SP500
        };
        singleSymbol("AAPL"); // for single test
        Context.symbols=getSymbols();
        //buildCache(Context.basicTimeframes, false);
        RunUtils.testStrategiesOnSpecificDate_("16.03.2022");

        }
```

take a look into console log:

```text
16.03.2022 ThreeDisplays_Buy_1 = OK
16.03.2022 ThreeDisplays_Buy_2 = OK
16.03.2022 ThreeDisplays_Buy_3 = STOCH_D_WAS_NOT_STRONG_OVERSOLD_RECENTLY_SCREEN_2
16.03.2022 ThreeDisplays_Buy_4 = LAST_QUOTES_NOT_ASCENDING_SCREEN_1
16.03.2022 ThreeDisplays_Buy_5 = HISTOGRAM_NOT_ASCENDING_SCREEN_2
16.03.2022 ThreeDisplays_Buy_6 = QUOTES_NOT_BELOW_EMA_SCREEN_2
16.03.2022 ThreeDisplays_Buy_7 = OK
16.03.2022 ThreeDisplays_Buy_8 = QUOTES_NOT_BELOW_EMA_SCREEN_2
16.03.2022 ThreeDisplays_Buy_9 = QUOTES_NOT_BELOW_EMA_SCREEN_2
16.03.2022 FirstScreen_Buy_1 = NOT_ALL_NEEDED_QUOTES_ABOUT_EMA_SCREEN_1
16.03.2022 ThreeDisplays_Buy_Bollinger_1 = OK
16.03.2022 ThreeDisplays_Buy_Bollinger_1_2 = OK
16.03.2022 ThreeDisplays_Buy_Bollinger_2 = QUOTE_2_NOT_BELOW_BOLLINGER_BOTTOM_SCREEN_2
16.03.2022 ThreeDisplays_Buy_Bollinger_3 = BOLLINGER_BOTTOM_NOT_ASCENDING_SCREEN_2
```

as we can see, five strategies would have responded that day, and the rest refused for various reasons.

#### Comparing different strategies

There is an option how to compare the performance of several strategies by
using `RunUtils.buildTasksAndStrategiesSummary_()`

In the following example it takes the random 30 stocks out of SP500 list and runs serveral selected stratgies over them:

```java
    public static void main(String[]args)throws Exception{
        Context.aggregationTimeframe=Timeframe.DAY;
        Context.source=new SymbolsSource[]{
        SymbolsSource.SP500_RANDOM // to use random stocks out of the list you have to build the full cache first
        };
        Context.symbols=getSymbols();
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

it means `ThreeDisplays_Buy_4` is the winner as it gave 218 profitable positions and just 31 were closed by StopLoss
over the last 5 years (can be extended) tested on 30 random stocks.

In terms of profit, the strategy `ThreeDisplays_Buy_7` has won, but look how many signals it produced: 632 + 237. The
TP/SL ratio is still very good, but this strategy suits for more aggressive trading.

#### How to configure exit rules

There are other TP/SL strategies, including volatile, that will follow the price based on some rules (but never set the
SL lower or course).

The full list is in packages `com.kishlaly.ta.analyze.testing.tp` and `com.kishlaly.ta.analyze.testing.ls`

By deafult I use these exit rules as they show more generic results:

* fixed `StopLossFixedPrice(0.27)` which finds the minimal quote from the last 20 and sets SL 27 cents below
* fixed `TakeProfitFixedKeltnerTop(70)` which stops position when the next quote reached 70% of the height between
  average and top of Keltner channel indicator

`RunUtils.buildTasksAndStrategiesSummary_()` also produced a more detailed report located at `data/stats` with output
like

```text
signal 2019-03-11T09:30-04:00[US/Eastern]
	SL: 53.95
	TP: 57.61
	open price: 56.33
	close price: 57.61
	closed: 2019-03-13T09:30-04:00[US/Eastern]
	profitable: true
	gap up: false
	gap down: false


signal 2018-09-27T09:30-04:00[US/Eastern]
	SL: 62.57
	TP: 65.65
	open price: 64.28
	close price: 62.57
	closed: 2018-10-11T09:30-04:00[US/Eastern]
	profitable: false
	gap up: false
	gap down: false
```
