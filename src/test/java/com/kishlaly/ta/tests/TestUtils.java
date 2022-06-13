package com.kishlaly.ta.tests;

import static org.assertj.core.api.Assertions.assertThat;

public class TestUtils {

    public static void checkPropertiesAndValues(Object object, String[] properties, Object[] values) {
        if (properties.length != values.length) {
            throw new RuntimeException("Inconsistent size of properties and values");
        }
        assertThat(object).isNotNull();
        for (int i = 0; i < properties.length; i++) {
            assertThat(object).hasFieldOrProperty(properties[i]);
            assertThat(object).extracting(properties).doesNotContainNull().containsExactly(values);
        }
    }

}
