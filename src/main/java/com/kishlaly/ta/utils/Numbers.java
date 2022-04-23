package com.kishlaly.ta.utils;

import java.math.BigDecimal;

public class Numbers {

    public static double round(double value) {
        if (Double.isNaN(value) || value == 0) {
            return 0d;
        }
        return BigDecimal.valueOf(value).setScale(2, BigDecimal.ROUND_UP).doubleValue();
    }

    public static int roundDown(double value) {
        if (Double.isNaN(value) || value == 0) {
            return 0;
        }
        return BigDecimal.valueOf(value).setScale(0, BigDecimal.ROUND_DOWN).intValue();
    }

    public static double roi(double initialCost, double currentCost) {
        double result = (currentCost - initialCost) / initialCost * 100;
        return round(result);
    }

    public static double percent(double n, double N) {
        return round(n * 100 / N);
    }

}
