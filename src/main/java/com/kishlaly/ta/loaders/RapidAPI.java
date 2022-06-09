package com.kishlaly.ta.loaders;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.ResponseBody;
import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.utils.Context;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Vladimir Kishlaly
 * @since 26.11.2021
 */
public class RapidAPI {

    private static Gson gson = new Gson();

    private static String getURL(String symbol) {
        switch (Context.timeframe) {
            case HOUR:
                return "https://yh-finance.p.rapidapi.com/stock/v2/get-chart?interval=60m&symbol=" + symbol + "&range=1mo&region=US";
            case DAY:
            default:
                return "https://yh-finance.p.rapidapi.com/stock/v2/get-chart?interval=1d&symbol=" + symbol + "&range=3mo&region=US";
        }
    }

    public static List<Quote> load(String symbol) {
        List<Quote> quotes = new ArrayList<>();
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(getURL(symbol))
                    .get()
                    .addHeader("x-rapidapi-host", "yh-finance.p.rapidapi.com")
                    .addHeader("x-rapidapi-key", "")
                    .build();

            ResponseBody body = client.newCall(request).execute().body();

            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            Map<String, Object> map = gson.fromJson(body.string(), type);

            LinkedTreeMap m1 = (LinkedTreeMap) map.get("chart");
            ArrayList a1 = (ArrayList) m1.get("result");
            LinkedTreeMap m2 = (LinkedTreeMap) a1.get(0);

            LinkedTreeMap meta = (LinkedTreeMap) m2.get("meta");
            String exchangeTimezoneName = (String) meta.get("exchangeTimezoneName");

            ArrayList<Double> timestamps = (ArrayList) m2.get("timestamp");
            timestamps.forEach(entry -> {
//                Quote quote = new Quote();
//                long timestamp = entry.longValue();
//                quote.exchangeTimezome = exchangeTimezoneName;
//                quote.setTimestamp(timestamp);
//                quotes.add(quote);
            });

            LinkedTreeMap indicators = (LinkedTreeMap) m2.get("indicators");
            ArrayList quotesRaw = (ArrayList) indicators.get("quote");
            LinkedTreeMap quotesMap = (LinkedTreeMap) quotesRaw.get(
                    0); // high, open, close, low, volume

            ArrayList<Double> h = (ArrayList<Double>) quotesMap.get("high");
            for (int i = 0; i < h.size(); i++) {
                quotes.get(i).setHigh(h.get(i));
            }

            ArrayList<Double> o = (ArrayList<Double>) quotesMap.get("open");
            for (int i = 0; i < o.size(); i++) {
                quotes.get(i).setOpen(o.get(i));
            }

            ArrayList<Double> c = (ArrayList<Double>) quotesMap.get("close");
            for (int i = 0; i < c.size(); i++) {
                quotes.get(i).setClose(c.get(i));
            }

            ArrayList<Double> l = (ArrayList<Double>) quotesMap.get("low");
            for (int i = 0; i < l.size(); i++) {
                quotes.get(i).setLow(l.get(i));
            }

            ArrayList<Double> v = (ArrayList<Double>) quotesMap.get("volume");
            for (int i = 0; i < v.size(); i++) {
                quotes.get(i).setVolume(v.get(i));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return quotes;
    }

}
