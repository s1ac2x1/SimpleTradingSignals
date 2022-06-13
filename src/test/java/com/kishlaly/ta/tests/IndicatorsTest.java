package com.kishlaly.ta.tests;

import com.kishlaly.ta.cache.CacheReader;
import com.kishlaly.ta.model.indicators.EMA;
import com.kishlaly.ta.model.indicators.Indicator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.kishlaly.ta.tests.TestConstants.AAPL_QUOTES_SIZE;
import static org.assertj.core.api.Assertions.assertThat;

public class IndicatorsTest {

    @Test
    public void testEMA13() {
        List<EMA> ema13Values = CacheReader.calculateIndicatorFromCachedQuotes("AAPL", Indicator.EMA13);
        assertThat(ema13Values)
                .isNotNull()
                .hasSize(AAPL_QUOTES_SIZE);
        TestUtils.checkPropertiesAndValues(
                ema13Values.get(ema13Values.size() - 1),
                new String[]{"value", "timestamp", "nativeDate"},
                new Object[]{147.13500860422104d, 1654695000L, "2022-06-08T09:30-04:00[US/Eastern]"}
        );
    }

}
