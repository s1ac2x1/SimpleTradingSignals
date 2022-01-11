package com.kishlaly.ta.utils;

import java.math.BigDecimal;

public class Numbers {

    public static double round(double value) {
        if (Double.isNaN(value)) {
            return 0d;
        }
        return BigDecimal.valueOf(value).setScale(2, BigDecimal.ROUND_UP).doubleValue();
    }

    public static double roi(double initialCost, double currentCost) {
        double result = (currentCost - initialCost) / initialCost * 100;
        return round(result);
    }

}
