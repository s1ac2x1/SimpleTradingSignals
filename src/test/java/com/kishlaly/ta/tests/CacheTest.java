package com.kishlaly.ta.tests;

import com.kishlaly.ta.cache.CacheReader;
import com.kishlaly.ta.model.Quote;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CacheTest {

    @Test
    public void loadQuotesFromDiskCacheTest() throws IOException {
        List<Quote> quotes = CacheReader.loadQuotesFromDiskCache("AAPL");
        assertThat(quotes)
                .isNotNull()
                .hasSize(1368);
    }

    @Test
    public void quoteStructureTest() throws IOException {
        List<Quote> quotes = CacheReader.loadQuotesFromDiskCache("AAPL");
        Quote lastQuote = quotes.get(quotes.size() - 1);
        System.out.println(lastQuote);
        assertThat(lastQuote)
                .isNotNull()
                .hasFieldOrProperty("open")
                .hasFieldOrProperty("close")
                .hasFieldOrProperty("high")
                .hasFieldOrProperty("low")
                .hasFieldOrProperty("volume")
                .hasFieldOrProperty("timestamp");

        assertThat(lastQuote)
                .extracting("open", "close", "high", "low", "volume", "timestamp")
                .doesNotContainNull()
                .containsExactly(148.58d, 147.96d, 149.87d, 147.46d, 5.3844022E7, 1654695000L);
    }

}
