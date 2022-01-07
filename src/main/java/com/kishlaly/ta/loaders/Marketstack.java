package com.kishlaly.ta.loaders;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.ResponseBody;
import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.marketstack.MarketstackResponse;
import com.kishlaly.ta.utils.Context;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Vladimir Kishlaly
 * @since 26.11.2021
 */
public class Marketstack {

    private static Gson gson = new Gson();

    public static List<Quote> load(String symbol) {
        List<Quote> quotes = new ArrayList<>();
        String url = getURL(symbol);
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            ResponseBody body = client.newCall(request).execute().body();
            MarketstackResponse response = gson.fromJson(
                    body.string(),
                    new TypeToken<MarketstackResponse>() {
                    }.getType());
            response.data.stream().filter(data -> data.close != null).forEach(data -> {
                Quote quote = new Quote();
                quote.setTimestamp(Instant.ofEpochMilli(data.date.getTime()).getEpochSecond());
                quote.setOpen(data.open);
                quote.setClose(data.close);
                quote.setHigh(data.high);
                quote.setLow(data.low);
                quote.setVolume(data.volume);
            });
        } catch (Exception e) {
            System.out.println("Couldn't get quotes");
        }
        return quotes;
    }


    private static String getURL(String symbol) {
        String url;
        switch (Context.timeframe) {
            case DAY: {
                url =
                        "https://api.marketstack.com/v1/eod?access_key=92b578871cab068c1973f69275ba08ee&symbols=" + symbol;
                break;
            }
            case HOUR: {
                url = "https://api.marketstack.com/v1/intraday?access_key=92b578871cab068c1973f69275ba08ee&interval=1hour&symbols=" + symbol;
                break;
            }
            default: {
                url = "https://api.marketstack.com/v1/eod?access_key=92b578871cab068c1973f69275ba08ee&symbols=" + symbol;
            }
        }
        return url;
    }

}
