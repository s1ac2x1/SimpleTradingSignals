package com.kishlaly.ta.tests;

import com.kishlaly.ta.cache.CacheReader;
import com.kishlaly.ta.model.Quote;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static com.kishlaly.ta.tests.TestConstants.AAPL_QUOTES_SIZE;
import static org.assertj.core.api.Assertions.assertThat;

public class CacheTest {

    @Test
    public void loadQuotesFromDiskCacheTest() throws IOException {
        List<Quote> quotes = CacheReader.loadQuotesFromDiskCache("AAPL");
        assertThat(quotes)
                .isNotNull()
                .hasSize(AAPL_QUOTES_SIZE);
    }

    @Test
    public void quoteStructureTest() throws IOException {
        List<Quote> quotes = CacheReader.loadQuotesFromDiskCache("AAPL");
        TestUtils.checkPropertiesAndValues(
                quotes.get(quotes.size() - 1),
                new String[]{"open", "close", "high", "low", "volume", "timestamp"},
                new Object[]{148.58d, 147.96d, 149.87d, 147.46d, 5.3844022E7, 1654695000L}
        );
    }

}
