package com.kishlaly.ta.analyze.tasks;

/**
 * For a bullish divergence, the stock price must be above $3; for a bearish divergence, it must be above $10. Trading volume on
 * stock should be above 500k a day (low trading volume means weak interest and frequent
 * sudden price swings).
 * <p>
 * Indicators:
 * MACD (12 26 9 close)
 * EMA26 for larger timeframe to filter downtrends
 * <p>
 * The search for bullish divergences occurs in this sequence:
 * 1. The MACD histogram goes down to the lowest minimum on the 100 bar segment
 * (you can set your own value). This is how the bottom of a potential bullish
 * A-B-C divergence.
 * 2. The MACD histogram crosses the zero line from bottom to top, "breaking the bear's backbone". So
 * The top of a potential bullish divergence is determined.
 * 3. When the stock reaches a new 100-day low, the MACD histogram crosses the nought line again.
 * the nought line again, but from the top down. This is the point where the scanner marks the stock.
 * Example: https://drive.google.com/file/d/1pd7Y92O3sMRRKHsTbsFoR6uYlhW33CyP/view?usp=sharing
 *
 * @author Vladimir Kishlaly
 * @since 18.11.2021
 */
public class Divergencies extends AbstractTaskJava {

    public static class BullishConfig {
        public static boolean ALLOW_ON_BEARISH_TREND = true; // divergences often occur on horizontal long-term trends
        public static boolean ALLOW_MULTIPLE_ISLANDS = true;
        public static int MAX_TAIL_SIZE = 7;
        public static int SECOND_BOTTOM_RATIO = 80;
    }

}
