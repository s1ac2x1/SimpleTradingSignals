package com.kishlaly.ta.misc;

import com.kishlaly.ta.model.QuoteJava;
import com.kishlaly.ta.model.TimeframeJava;
import com.kishlaly.ta.utils.Bars;
import com.kishlaly.ta.utils.Context;
import org.ta4j.core.*;
import org.ta4j.core.analysis.criteria.pnl.GrossReturnCriterion;
import org.ta4j.core.indicators.CCIIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.OverIndicatorRule;
import org.ta4j.core.rules.UnderIndicatorRule;

import java.util.List;

import static com.kishlaly.ta.cache.CacheReader.loadQuotesFromDiskCache;

public class CCICorrectionStrategy {

    public static Strategy buildStrategy(BarSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Series cannot be null");
        }

        CCIIndicator longCci = new CCIIndicator(series, 200);
        CCIIndicator shortCci = new CCIIndicator(series, 5);
        Num plus100 = series.numOf(100);
        Num minus100 = series.numOf(-100);

        Rule entryRule = new OverIndicatorRule(longCci, plus100) // Bull trend
                .and(new UnderIndicatorRule(shortCci, minus100)); // Signal

        Rule exitRule = new UnderIndicatorRule(longCci, minus100) // Bear trend
                .and(new OverIndicatorRule(shortCci, plus100)); // Signal

        Strategy strategy = new BaseStrategy(entryRule, exitRule);
        strategy.setUnstablePeriod(5);
        return strategy;
    }

    public static void main(String[] args) {
        Context.aggregationTimeframe = TimeframeJava.DAY;
        Context.timeframe = TimeframeJava.DAY;
        String symbol = "TER";
        List<QuoteJava> quotes = loadQuotesFromDiskCache(symbol);
        BarSeries barSeries = Bars.build(quotes);

        Strategy strategy = buildStrategy(barSeries);

        BarSeriesManager seriesManager = new BarSeriesManager(barSeries);
        TradingRecord tradingRecord = seriesManager.run(strategy);
        System.out.println("Number of positions for the strategy: " + tradingRecord.getPositionCount());

        System.out.println("Total return for the strategy: " + new GrossReturnCriterion().calculate(barSeries, tradingRecord));
    }

}
