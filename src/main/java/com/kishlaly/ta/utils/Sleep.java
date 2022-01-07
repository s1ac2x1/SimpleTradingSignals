package com.kishlaly.ta.utils;

/**
 * @author Vladimir Kishlaly
 * @since 18.11.2021
 */
public class Sleep {

    public static void sleep() {
        try {
            switch (Context.api) {
                case RAPID:
                    Thread.sleep(50);
                    break;
                case MARKETSTACK:
                    Thread.sleep(200);
                    break;
                case ALPHAVANTAGE:
                default:
                    //Thread.sleep(12 * 1001);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
