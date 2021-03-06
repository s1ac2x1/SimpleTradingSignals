package com.kishlaly.ta.utils;

import com.kishlaly.ta.model.Quote;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeries;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

import static com.kishlaly.ta.utils.Dates.getTimeInExchangeZone;

/**
 * @author Vladimir Kishlaly
 * @since 26.11.2021
 */
public class Bars {

    public static BarSeries build(List<Quote> quotes) {
        BarSeries initialBarSeries = new BaseBarSeries();
        quotes.forEach(quote -> {
            try {
                ZonedDateTime timestamp = getTimeInExchangeZone(quote.getTimestamp(), quote.exchangeTimezome);
                initialBarSeries.addBar(
                        Duration.ofMinutes(getBarDurationInMinutes()),
                        timestamp,
                        quote.getOpen(),
                        quote.getHigh(),
                        quote.getLow(),
                        quote.getClose(),
                        quote.getVolume());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
        return initialBarSeries;
    }

    public static int getBarDurationInMinutes() {
        switch (Context.timeframe) {
            case DAY:
                return Context.workingTime;
            case WEEK:
                return Context.workingTime * 5;
            case HOUR:
            default:
                return 60;
        }
    }

}
