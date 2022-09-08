package com.kishlaly.ta.tests;

import com.kishlaly.ta.cache.CacheReaderJava;
import com.kishlaly.ta.model.indicators.EMAJava;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.kishlaly.ta.tests.TestConstants.AAPL_DAILY_QUOTES_SIZE;
import static org.assertj.core.api.Assertions.assertThat;

public class IndicatorsTest {

    @Test
    public void testEMA13() {
        List<EMAJava> ema13Values = CacheReaderJava.calculateIndicatorFromCachedQuotes("AAPL", IndicatorJava.EMA13);
        assertThat(ema13Values)
                .isNotNull()
                .hasSize(AAPL_DAILY_QUOTES_SIZE);
        TestUtils.checkPropertiesAndValues(
                ema13Values.get(ema13Values.size() - 1),
                new String[]{"value", "timestamp", "nativeDate"},
                new Object[]{147.13500860422104d, 1654695000L, "2022-06-08T09:30-04:00[US/Eastern]"}
        );
    }

}
