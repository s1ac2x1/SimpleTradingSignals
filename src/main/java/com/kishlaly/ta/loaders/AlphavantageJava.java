package com.kishlaly.ta.loaders;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.kishlaly.ta.model.QuoteJava;
import com.kishlaly.ta.model.TimeframeJava;
import com.kishlaly.ta.model.indicators.EMAJava;
import com.kishlaly.ta.model.indicators.MACDJava;
import com.kishlaly.ta.model.indicators.StochJava;
import com.kishlaly.ta.utils.ContextJava;
import com.kishlaly.ta.utils.DatesJava;
import com.kishlaly.ta.utils.QuotesJava;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.ResponseBody;

import java.lang.reflect.Type;
import java.util.*;

import static java.lang.Double.parseDouble;

/**
 * For stocks, the last price is always the end of the previous day, regardless of the timeframe
 * There are intraday prices for forex and crypto
 * <p>
 * The timezone is always US/Eastern
 * https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=IBM&interval=60min&outputsize=full&apikey=
 * https://www.alphavantage.co/query?function=TIME_SERIES_WEEKLY&symbol=TER&&apikey=
 * https://www.alphavantage.co/query?function=EMA&symbol=TER&apikey=&series_type=close&time_period=26&interval=weekly
 *
 * @author Vladimir Kishlaly
 * @since 26.11.2021
 */
public class AlphavantageJava {

    private static Gson gson = new Gson();
    private static String KEY = "F4ZNUB0VYAAMTSLP"; // premium, 75 req/min, expired

    /**
     * Load from alphavantage.co
     * <p>
     * Assuming Exchange Timezone = "US/Eastern"
     */
    public static List<QuoteJava> loadQuotes(String symbol, TimeframeJava timeframe) {
        List<QuoteJava> quotes = new ArrayList<>();
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
                quotes.add(new QuoteJava(
                        DatesJava.getTimeInExchangeZone(day, QuoteJava.exchangeTimezome).toEpochSecond(),
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
        if (quotes.size() < QuotesJava.resolveMinBarsCount(timeframe)) {
            return Collections.emptyList();
        }
        Collections.sort(quotes, Comparator.comparing(QuoteJava::getTimestamp));
        return quotes;
    }

    // not used as all indicators are being calculated locally
    public static List<MACDJava> loadMACD(String symbol) {
        List<MACDJava> result = new ArrayList<>();
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
                if (ContextJava.timeframe == TimeframeJava.HOUR) {
                    day += ":00";
                }
                Double macdValue = parseDouble(v.get("MACD"));
                Double macdSignalValue
                        = Double.parseDouble(v.get("MACD_Signal"));
                Double macdHist = parseDouble(v.get("MACD_Hist"));
                result.add(new MACDJava(DatesJava.getTimeInExchangeZone(day, QuoteJava.exchangeTimezome).toEpochSecond(), macdValue, macdSignalValue, macdHist));
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Collections.emptyList();
        }
        if (result.size() < 10) {
            return Collections.emptyList();
        }
        Collections.sort(result, Comparator.comparing(MACDJava::getTimestamp));
        return result;
    }

    // not used as all indicators are being calculated locally
    public static List<EMAJava> loadEMA(String symbol, int period) {
        List<EMAJava> result = new ArrayList<>();
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
                if (ContextJava.timeframe == TimeframeJava.HOUR) {
                    day += ":00";
                }
                Double ema = parseDouble(v.get("EMA"));
                result.add(new EMAJava(DatesJava.getTimeInExchangeZone(day, QuoteJava.exchangeTimezome).toEpochSecond(), ema));
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Collections.emptyList();
        }
        if (result.size() < 10) {
            return Collections.emptyList();
        }
        Collections.sort(result, Comparator.comparing(EMAJava::getTimestamp));
        return result;
    }

    // not used as all indicators are being calculated locally
    public static List loadStoch(String symbol) {
        List<StochJava> result = new ArrayList<>();
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
                if (ContextJava.timeframe == TimeframeJava.HOUR) {
                    day += ":00";
                }
                Double slowD = parseDouble(v.get("SlowD"));
                Double slowK = parseDouble(v.get("SlowK"));
                result.add(new StochJava(DatesJava.getTimeInExchangeZone(day, QuoteJava.exchangeTimezome).toEpochSecond(), slowD, slowK));
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Collections.emptyList();
        }
        if (result.size() < 10) {
            return Collections.emptyList();
        }
        Collections.sort(result, Comparator.comparing(StochJava::getTimestamp));
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

    private static String getQuotesUrl(String symbol, TimeframeJava timeframe) {
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
        switch (ContextJava.timeframe) {
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
