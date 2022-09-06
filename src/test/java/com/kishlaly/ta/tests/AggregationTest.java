package com.kishlaly.ta.tests;

import com.kishlaly.ta.cache.CacheReader;
import com.kishlaly.ta.model.QuoteJava;
import com.kishlaly.ta.utils.Quotes;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AggregationTest {

    @Test
    public void dayToWeekAggregationTest() {
        List<QuoteJava> dailyQuotes = CacheReader.loadQuotesFromDiskCache("AAPL");
        List<QuoteJava> weeklyQuotes = Quotes.dayToWeek(dailyQuotes);
        assertThat(weeklyQuotes).isNotNull().hasSize(296);
        TestUtils.checkPropertiesAndValues(
                weeklyQuotes.get(weeklyQuotes.size() - 1),
                new String[]{"open", "close", "high", "low", "volume", "timestamp"},
                new Object[]{149.07d, 145.38d, 151.74d, 144.46d, 3.38923395E8, 1654003800L}
        );
    }

}
