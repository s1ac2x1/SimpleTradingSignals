This project manifested itself as an auxiliary utility for analyzing historical stock price data. For a quick start I decided to use the excellent library for technical analysis https://github.com/ta4j/ta4j.

After a while I found out that my project can be used for searching behavioral patterns of price charts, and now I am adding new strategies in my spare time

The main inspirations: Alexander Elder and Thomas Bulkowski

From Mr. Elder's book I get great ideas to use several timeframes, and Mr. Bulkowski's books, as I think, are just created to be programmed into automatic scanners

Data source provider used inside is https://www.alphavantage.co/
This is a paid service, however, I've included historical quotes for SP500 list.

Key points:
* build cache
* analyze using various strategies
* the outup will be in `data/signal` folder
* debug information stored in `data/debug` folder
* `RunUtils` has a lot of useful methods, among them are:
  * `testOneStrategy` for single test
  * `testStrategiesOnSpecificDate_` for capturing the output of the all strategies on specific date. Useful when you see a good potential entrypoint on the chart and want to see which strategies are able to give a singal
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

More detailed documentation on how to use this program is still in progress...