package com.kishlaly.ta.loaders;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.Timeframe;
import com.kishlaly.ta.model.indicators.EMA;
import com.kishlaly.ta.model.indicators.MACD;
import com.kishlaly.ta.model.indicators.Stoch;
import com.kishlaly.ta.utils.Context;
import com.kishlaly.ta.utils.Dates;
import com.kishlaly.ta.utils.Quotes;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.ResponseBody;

import java.lang.reflect.Type;
import java.util.*;

import static java.lang.Double.parseDouble;

/**
 * Для акций последняя цена всегда за конец прошлого дня независимо от таймфрейма
 * Для форекса и крипты есть внутредневные цены
 * <p>
 * Таймзона всегда US/Eastern (проверить для форекса)
 * <p>
 * F4ZNUB0VYAAMTSLP
 * <p>
 * https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=IBM&interval=60min&outputsize=full&apikey=F4ZNUB0VYAAMTSLP
 * https://www.alphavantage.co/query?function=TIME_SERIES_WEEKLY&symbol=TER&&apikey=F4ZNUB0VYAAMTSLP
 * https://www.alphavantage.co/query?function=EMA&symbol=TER&apikey=F4ZNUB0VYAAMTSLP&series_type=close&time_period=26&interval=weekly
 *
 * @author Vladimir Kishlaly
 * @since 26.11.2021
 */
public class Alphavantage {

    private static Gson gson = new Gson();
    private static String KEY = "F4ZNUB0VYAAMTSLP"; // premium, 75 req/min, expired

    /**
     * Load from alphavantage.co
     * <p>
     * Assuming Exchange Timezone = "US/Eastern"
     */
    public static List<Quote> loadQuotes(String symbol, Timeframe timeframe) {
        List<Quote> quotes = new ArrayList<>();
        String url = getQuotesUrl(symbol, timeframe);
        try {
            Map<String, Object> map = getResponse(url);
            if (map == null) {
                return Collections.emptyList();
            }
            String key = "Time Series (Daily)";
            switch (timeframe) {
                case WEEK:
                    key = "Weekly Time Series";
                    break;
                case DAY:
                    key = "Time Series (Daily)";
                    break;
                case HOUR:
                    key = "Time Series (60min)";
            }
            LinkedTreeMap<String, LinkedTreeMap<String, String>> rawQuotes = (LinkedTreeMap) map.get(
                    key);
            rawQuotes.forEach((k, v) -> {
                String day = k;
                String open = v.get("1. open");
                String high = v.get("2. high");
                String low = v.get("3. low");
                String close = v.get("4. close");
                String volume = v.get("5. volume");
                quotes.add(new Quote(
                        Dates.getTimeInExchangeZone(day, Quote.exchangeTimezome).toEpochSecond(),
                        parseDouble(high),
                        parseDouble(open),
                        parseDouble(close),
                        parseDouble(low),
                        parseDouble(volume)));
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Collections.emptyList();
        }
        if (quotes.size() < Quotes.resolveMinBarsCount(timeframe)) {
            return Collections.emptyList();
        }
        Collections.sort(quotes, Comparator.comparing(Quote::getTimestamp));
        return quotes;
    }

    public static List<MACD> loadMACD(String symbol) {
        List<MACD> result = new ArrayList<>();
        String url = "https://www.alphavantage.co/query?function=MACD&symbol=" + symbol + "&interval=" + getInterval() + "&series_type=close&apikey=" + KEY;
        try {
            Map<String, Object> map = getResponse(url);
            if (map == null) {
                return Collections.emptyList();
            }
            String key = "Technical Analysis: MACD";
            LinkedTreeMap<String, LinkedTreeMap<String, String>> rawQuotes = (LinkedTreeMap) map.get(
                    key);
            rawQuotes.forEach((k, v) -> {
                String day = k;
                if (Context.timeframe == Timeframe.HOUR) {
                    day += ":00";
                }
                Double macdValue = parseDouble(v.get("MACD"));
                Double macdSignalValue
                        = Double.parseDouble(v.get("MACD_Signal"));
                Double macdHist = parseDouble(v.get("MACD_Hist"));
                result.add(new MACD(Dates.getTimeInExchangeZone(day, Quote.exchangeTimezome).toEpochSecond(), macdValue, macdSignalValue, macdHist));
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Collections.emptyList();
        }
        if (result.size() < 10) {
            return Collections.emptyList();
        }
        Collections.sort(result, Comparator.comparing(MACD::getTimestamp));
        return result;
    }

    public static List<EMA> loadEMA(String symbol, int period) {
        List<EMA> result = new ArrayList<>();
        String url = "https://www.alphavantage.co/query?function=EMA&symbol=" + symbol + "&time_period=" + period + "&interval=" + getInterval() + "&series_type=close&apikey=" + KEY;
        try {
            Map<String, Object> map = getResponse(url);
            if (map == null) {
                return Collections.emptyList();
            }
            String key = "Technical Analysis: EMA";
            LinkedTreeMap<String, LinkedTreeMap<String, String>> rawQuotes = (LinkedTreeMap) map.get(
                    key);
            rawQuotes.forEach((k, v) -> {
                String day = k;
                if (Context.timeframe == Timeframe.HOUR) {
                    day += ":00";
                }
                Double ema = parseDouble(v.get("EMA"));
                result.add(new EMA(Dates.getTimeInExchangeZone(day, Quote.exchangeTimezome).toEpochSecond(), ema));
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Collections.emptyList();
        }
        if (result.size() < 10) {
            return Collections.emptyList();
        }
        Collections.sort(result, Comparator.comparing(EMA::getTimestamp));
        return result;
    }

    public static List loadStoch(String symbol) {
        List<Stoch> result = new ArrayList<>();
        String url = "https://www.alphavantage.co/query?function=STOCH&symbol=" + symbol + "&interval=" + getInterval() + "&fastkperiod=14&slowkperiod=1&slowdperiod=3&apikey=" + KEY;
        try {
            Map<String, Object> map = getResponse(url);
            if (map == null) {
                return Collections.emptyList();
            }
            String key = "Technical Analysis: STOCH";
            LinkedTreeMap<String, LinkedTreeMap<String, String>> rawQuotes = (LinkedTreeMap) map.get(
                    key);
            rawQuotes.forEach((k, v) -> {
                String day = k;
                if (Context.timeframe == Timeframe.HOUR) {
                    day += ":00";
                }
                Double slowD = parseDouble(v.get("SlowD"));
                Double slowK = parseDouble(v.get("SlowK"));
                result.add(new Stoch(Dates.getTimeInExchangeZone(day, Quote.exchangeTimezome).toEpochSecond(), slowD, slowK));
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Collections.emptyList();
        }
        if (result.size() < 10) {
            return Collections.emptyList();
        }
        Collections.sort(result, Comparator.comparing(Stoch::getTimestamp));
        return result;
    }

    private static Map<String, Object> getResponse(String url) {
        Map<String, Object> response = null;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            ResponseBody body = client.newCall(request).execute().body();
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            response = gson.fromJson(body.string(), type);
        } catch (Exception e) {
            System.out.println("Error while getting API response. " + e.getMessage());
        }
        return response;
    }

    private static String getQuotesUrl(String symbol, Timeframe timeframe) {
        String url;
        switch (timeframe) {
            case WEEK: {
                url = "https://www.alphavantage.co/query" +
                        "?function=TIME_SERIES_WEEKLY" +
                        "&symbol=" + symbol +
                        "&apikey=" + KEY;
                break;
            }
            case DAY: {
                url =
                        "https://www.alphavantage.co/query" +
                                "?function=TIME_SERIES_DAILY" +
                                "&symbol=" + symbol +
                                "&outputsize=full" +
                                "&apikey=" + KEY;
                break;
            }
            case HOUR: {
                url =
                        "https://www.alphavantage.co/query" +
                                "?function=TIME_SERIES_INTRADAY" +
                                "&interval=60min" +
                                "&symbol=" + symbol +
                                "&outputsize=full" +
                                "&apikey=" + KEY;
                break;
            }
            default: {
                url = "";
            }
        }
        return url;
    }

    private static String getInterval() {
        switch (Context.timeframe) {
            case HOUR:
                return "60min";
            case WEEK:
                return "weekly";
            case DAY:
            default:
                return "daily";
        }
    }

}
