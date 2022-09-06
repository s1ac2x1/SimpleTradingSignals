package com.kishlaly.ta.cache;

import com.google.common.collect.Lists;
import com.kishlaly.ta.analyze.TaskType;
import com.kishlaly.ta.analyze.tasks.blocks.groups.BlocksGroup;
import com.kishlaly.ta.analyze.testing.sl.*;
import com.kishlaly.ta.analyze.testing.tp.TakeProfitFixedKeltnerTop;
import com.kishlaly.ta.analyze.testing.tp.TakeProfitStrategy;
import com.kishlaly.ta.analyze.testing.tp.TakeProfitVolatileKeltnerTop;
import com.kishlaly.ta.loaders.Alphavantage;
import com.kishlaly.ta.analyze.testing.HistoricalTesting;
import com.kishlaly.ta.model.QuoteJava;
import com.kishlaly.ta.model.Timeframe;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.utils.Context;
import com.kishlaly.ta.utils.FilesUtil;
import com.kishlaly.ta.utils.Numbers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.kishlaly.ta.analyze.testing.TaskTester.test;
import static com.kishlaly.ta.cache.CacheReader.*;
import static com.kishlaly.ta.utils.Context.*;
import static com.kishlaly.ta.utils.FilesUtil.writeToFile;

public class CacheBuilder {

    public static void buildCache(Timeframe[][] timeframes, boolean reloadMissed) {
        String folder = Context.outputFolder + fileSeparator + "cache";
        File directory = new File(folder);
        if (!directory.exists()) {
            directory.mkdir();
        }
        AtomicReference<Set<String>> symbols = new AtomicReference<>(Context.symbols);
        Arrays.stream(timeframes).forEach(screens -> {
            // only one timeframe is loaded Context.aggregationTimeframe
            Context.timeframe = aggregationTimeframe;
            if (reloadMissed) {
                symbols.set(getMissedSymbols());
            }
            cacheQuotes(symbols.get());
        });
        double p = limitPerMinute / parallelRequests;
        requestPeriod = (int) (p * 1000) + 1000; // +1 second for margin
        queueExecutor.scheduleAtFixedRate(() -> processQueue(), requestPeriod, requestPeriod, TimeUnit.MILLISECONDS);
        if (reloadMissed) {
            Arrays.stream(timeframes).forEach(screens -> {
                Arrays.stream(screens).forEach(screen -> {
                    Context.timeframe = screen;
                    File file = new File(getFolder() + fileSeparator + "missed.txt");
                    if (file.exists()) {
                        file.delete();
                    }
                });
            });
        }
    }

    public static void processQueue() {
        System.out.println("");
        int seconds = requests.size() * requestPeriod / 1000;
        int hours = seconds / 3600;
        int remainderSeconds = seconds - hours * 3600;
        int mins = remainderSeconds / 60;
        remainderSeconds = remainderSeconds - mins * 60;
        int secs = remainderSeconds;
        System.out.println(hours + ":" + mins + ":" + secs + " left...");
        if (requests.size() == 0) {
            queueExecutor.shutdownNow();
        }
        // to run new requests to the API, if not all of the last batch has been completed
        for (int i = 0; i < callsInProgress.size(); i++) {
            if (!callsInProgress.get(i).isDone()) {
                System.out.println("Previous batch is still in progress, skipping this round");
                return;
            }
        }
        LoadRequest request = requests.poll();
        if (request != null) {
            List<String> symbols = request.getSymbols();
            Timeframe timeframe = request.getTimeframe();
            Context.timeframe = timeframe;
            if (request.getType() == CacheType.QUOTE) {
                System.out.println("Loading " + timeframe.name() + " quotes...");
                symbols.forEach(symbol -> {
                    Future<?> future = apiExecutor.submit(() -> {
                        List<QuoteJava> quotes = Alphavantage.loadQuotes(symbol, timeframe);
                        if (!quotes.isEmpty()) {
                            saveQuote(symbol, quotes);
                        }
                    });
                    callsInProgress.add(future);
                });
            }
            if (request.getType() == CacheType.INDICATOR) {
                IndicatorJava indicator = request.getIndicator();
                System.out.println("Loading " + timeframe.name() + " " + indicator.name() + "...");
                symbols.forEach(symbol -> {
                    Future<?> future = apiExecutor.submit(() -> {
                        List indicatorValue = loadIndicatorFromProvider(symbol, indicator);
                        if (!indicatorValue.isEmpty()) {
                            saveIndicator(symbol, indicator, indicatorValue);
                        }
                    });
                    callsInProgress.add(future);
                });
            }
        }
    }

    // for each character set (SP500, NAGA, ...) creates a file best_{set}_{screen1}_{screen2}.txt
    // in this file lines like symbol=TaskType
    // it is assumed that each symbol corresponds to a TaskType, which showed the best result on historical data
    // When testing signals we used the base pair StopLossFixedPrice(0.27) and TakeProfitFixedKeltnerTop(100)
    public static void findBestStrategyForSymbols(TaskType task) {
        if (source.length > 1) {
            throw new RuntimeException("Only one symbols source please");
        }
        Timeframe[][] timeframes = {
                {Timeframe.WEEK, Timeframe.DAY},
        };
        List<HistoricalTesting> result = new ArrayList<>();
        Context.stopLossStrategy = new StopLossFixedPrice(0.27);
        Context.takeProfitStrategy = new TakeProfitFixedKeltnerTop(100);

        // TODO Here we need to test a Cartesian set of blocks
        //result.addAll(test(timeframes, task, BlocksGroup));

        Map<String, TaskType> winners = new HashMap<>();
        result.stream().collect(Collectors.groupingBy(HistoricalTesting::getSymbol))
                .entrySet().stream().forEach(bySymbol -> {
                    String symbol = bySymbol.getKey();
                    List<HistoricalTesting> testings = bySymbol.getValue();
                    Collections.sort(testings, Comparator.comparing(HistoricalTesting::getBalance));
                    HistoricalTesting best = testings.get(testings.size() - 1);
                    // TODO need to save the symbol--taskType--blocks mapping
                    winners.put(symbol, best.getTaskType());
                    System.out.println(symbol + " " + best.getTaskType().name() + " " + best.getBalance());
                });
        StringBuilder builder = new StringBuilder();
        winners.entrySet().stream().forEach(entry -> {
            builder.append(entry.getKey()).append("=").append(entry.getValue().name()).append(System.lineSeparator());
        });
        writeToFile("best_" + Context.source[0].name().toLowerCase() + "_" + timeframes[0][0].name().toLowerCase() + "_" + timeframes[0][1].name().toLowerCase() + ".txt", builder.toString());
    }

    public static void saveTable(List<HistoricalTesting> result) {
        StringBuilder table = new StringBuilder("<table>");
        result
                .stream()
                .collect(Collectors.groupingBy(HistoricalTesting::getSymbol))
                .entrySet().stream()
                .forEach(bySymbol -> {
                    String symbol = bySymbol.getKey();
                    table.append("<tr>");
                    table.append("<td style=\"vertical-align: left;\">" + symbol + "</td>");
                    table.append("<td>");
                    StringBuilder innerTable = new StringBuilder("<table>");
                    List<HistoricalTesting> testings = bySymbol.getValue();
                    Collections.sort(testings, Comparator.comparing(HistoricalTesting::getBalance));
                    testings.stream()
                            .collect(Collectors.groupingBy(HistoricalTesting::getBlocksGroup))
                            .entrySet().stream()
                            .forEach(byTask -> {
                                BlocksGroup blocksGroup = byTask.getKey();
                                List<HistoricalTesting> historicalTestings = byTask.getValue();
                                HistoricalTesting best = historicalTestings.get(historicalTestings.size() - 1);
                                innerTable.append("<tr>");
                                innerTable.append("<td style=\"vertical-align: top text-align: left;\">" + blocksGroup.getClass().getSimpleName() + "</td>");
                                innerTable.append("<td style=\"vertical-align: top text-align: center;\">&nbsp;&nbsp;&nbsp;</td>");

                                innerTable.append("<td style=\"vertical-align: top text-align: left; white-space: nowrap;\">" + best.printTPSLNumber() + "</td>");
                                innerTable.append("<td style=\"vertical-align: top text-align: center;\">&nbsp;&nbsp;&nbsp;</td>");

                                innerTable.append("<td style=\"vertical-align: top text-align: left; white-space: nowrap;\">" + best.printTPSLPercent() + "</td>");
                                innerTable.append("<td style=\"vertical-align: top text-align: center;\">&nbsp;&nbsp;&nbsp;</td>");

                                innerTable.append("<td style=\"vertical-align: top text-align: left;\">" + best.getBalance() + "</td>");
                                innerTable.append("<td style=\"vertical-align: top text-align: center;\">&nbsp;&nbsp;&nbsp;</td>");

                                innerTable.append("<td style=\"vertical-align: top text-align: left; white-space: nowrap;\">" + best.getStopLossStrategy() + "</td>");
                                innerTable.append("<td style=\"vertical-align: top text-align: center;\">&nbsp;&nbsp;&nbsp;</td>");

                                innerTable.append("<td style=\"vertical-align: top text-align: left; white-space: nowrap;\">" + best.getTakeProfitStrategy() + "</td>");
                                innerTable.append("<td style=\"vertical-align: top text-align: center;\">&nbsp;&nbsp;&nbsp;</td>");

                                innerTable.append("</tr>");
                            });
                    innerTable.append("</table>");
                    table.append(innerTable);
                    table.append("</td>");
                    table.append("</tr>");
                });
        table.append("</table>");
        writeToFile("tests/table.html", table.toString());
    }

    public static void buildTasksAndStrategiesSummary(Timeframe[][] timeframes,
                                                      TaskType task,
                                                      List<BlocksGroup> blocksGroups,
                                                      StopLossStrategy stopLossStrategy,
                                                      TakeProfitStrategy takeProfitStrategy) {
        List<HistoricalTesting> result = new ArrayList<>();
        int total = getSLStrategies().size() * getTPStrategies().size();
        AtomicInteger current = new AtomicInteger(1);
        if (stopLossStrategy == null || takeProfitStrategy == null) {
            getSLStrategies().forEach(sl -> {
                getTPStrategies().forEach(tp -> {
                    Context.stopLossStrategy = sl;
                    Context.takeProfitStrategy = tp;
                    System.out.println(current.get() + "/" + total + " " + sl + " / " + tp);
                    blocksGroups.forEach(group -> result.addAll(test(timeframes, task, group)));
                    current.getAndIncrement();
                });
            });
        } else {
            Context.stopLossStrategy = stopLossStrategy;
            Context.takeProfitStrategy = takeProfitStrategy;
            blocksGroups.forEach(group -> result.addAll(test(timeframes, task, group)));
        }
        saveTable(result);
        saveSummaryPerGroup(result);
    }

    private static void saveSummaryPerGroup(List<HistoricalTesting> result) {
        TreeMap<Double, String> balances = new TreeMap<>(Collections.reverseOrder());
        List<TPSL> tpSL = new ArrayList<>();
        List<ROI> roi = new ArrayList<>();
        Map<BlocksGroup, List<HistoricalTesting>> byGroup = result.stream().collect(Collectors.groupingBy(HistoricalTesting::getBlocksGroup));
        byGroup.entrySet().stream().forEach(entry -> {
            BlocksGroup group = entry.getKey();
            List<HistoricalTesting> testings = entry.getValue();
            double totalBalance = testings.stream().map(HistoricalTesting::getBalance).mapToDouble(Double::doubleValue).sum();
            totalBalance = Numbers.round(totalBalance);
            String groupName = group.getClass().getSimpleName();
            balances.put(totalBalance, groupName);
            long tp = testings.stream().mapToLong(t -> t.getProfitablePositionsCount()).sum();
            long sl = testings.stream().mapToLong(t -> t.getLossPositionsCount()).sum();
            tpSL.add(new TPSL(groupName, tp, sl));
            double roi_ = testings.stream().mapToDouble(t -> t.getAverageRoi()).average().orElse(0);
            roi.add(new ROI(groupName, roi_));
        });
        StringBuilder output = new StringBuilder();
        balances.forEach((k, v) -> output.append(v + ": " + k + System.lineSeparator()));
        output.append(System.lineSeparator());
        Comparator<TPSL> c = (o1, o2) -> {
            long l1 = o1.getSl() > 0 ? o1.getTp() / o1.getSl() : o1.getTp();
            long l2 = o2.getSl() > 0 ? o2.getTp() / o2.getSl() : o2.getTp();
            return (int) (l1 - l2);
        };
        tpSL.sort(c.reversed());
        tpSL.forEach(tpsl -> output.append(tpsl.groupName + ": TP/SL = " + tpsl.getTp() + "/" + tpsl.getSl() + System.lineSeparator()));
        output.append(System.lineSeparator());
        roi.sort(Comparator.comparing(ROI::getRoi).reversed());
        roi.forEach(r -> output.append(r.groupName + ": " + Numbers.round(r.getRoi()) + " %" + System.lineSeparator()));
        FilesUtil.writeToFile("tests/summary.txt", output.toString());
    }

    static class ROI {
        String groupName;
        double roi;

        public ROI(final String groupName, final double roi) {
            this.groupName = groupName;
            this.roi = roi;
        }

        public String getGroupName() {
            return this.groupName;
        }

        public double getRoi() {
            return this.roi;
        }
    }

    static class TPSL {
        String groupName;
        long tp;
        long sl;

        public TPSL(final String groupName, final long tp, final long sl) {
            this.groupName = groupName;
            this.tp = tp;
            this.sl = sl;
        }

        public String getGroupName() {
            return this.groupName;
        }

        public long getTp() {
            return this.tp;
        }

        public long getSl() {
            return this.sl;
        }
    }

    public static List<StopLossStrategy> getSLStrategies() {
        return new ArrayList<StopLossStrategy>() {{
            add(new StopLossFixedPrice(0.27));
            add(new StopLossFixedKeltnerBottom());
            add(new StopLossVolatileKeltnerBottom(80));
            add(new StopLossVolatileKeltnerBottom(100));
            add(new StopLossVolatileLocalMin(0.27));
            add(new StopLossVolatileATR());
        }};
    }

    public static List<TakeProfitStrategy> getTPStrategies() {
        return new ArrayList<TakeProfitStrategy>() {{
            add(new TakeProfitFixedKeltnerTop(80));
            add(new TakeProfitFixedKeltnerTop(100));
            add(new TakeProfitVolatileKeltnerTop(80));
            add(new TakeProfitVolatileKeltnerTop(100));
        }};
    }

    private static void saveIndicator(String symbol, IndicatorJava indicator, List values) {
        try {
            String folder = Context.outputFolder + "/cache/" + Context.timeframe.name().toLowerCase();
            File directory = new File(folder);
            if (!directory.exists()) {
                directory.mkdir();
            }
            String json = gson.toJson(values);
            Files.write(Paths.get(folder + "/" + symbol + "_" + indicator.name().toLowerCase() + ".txt"), json.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List loadIndicatorFromProvider(String symbol, IndicatorJava indicator) {
        switch (indicator) {
            case MACD:
                return Alphavantage.loadMACD(symbol);
            case EMA26:
                return Alphavantage.loadEMA(symbol, 26);
            case EMA13:
                return Alphavantage.loadEMA(symbol, 13);
            case STOCH:
                return Alphavantage.loadStoch(symbol);
            default:
                return Collections.emptyList();
        }
    }

    private static void cacheQuotes(Set<String> symbols) {
        List<String> symbolsToCache = removeCachedQuotesSymbols(symbols);
        if (symbolsToCache.isEmpty()) {
            System.out.println(Context.timeframe.name() + " quotes already cached");
            return;
        }
        Lists.partition(symbolsToCache, (int) parallelRequests)
                .forEach(chunk -> {
                    Optional<LoadRequest> existingRequest = requests.stream().filter(request -> request.getType() == CacheType.QUOTE
                            && request.getTimeframe() == Context.timeframe
                            && request.getSymbols().containsAll(chunk)).findFirst();
                    if (!existingRequest.isPresent()) {
                        requests.offer(new LoadRequest(CacheType.QUOTE, Context.timeframe, chunk));
                    } else {
                        System.out.println("Already in the queue: " + chunk.size() + " " + Context.timeframe.name() + " QUOTE");
                    }
                });
    }

    private static void cacheIndicators(Set<String> symbols, IndicatorJava[] indicators) {
        Arrays.stream(indicators).forEach(indicator -> {
            List<String> symbolsToCache = removeCachedIndicatorSymbols(symbols, indicator);
            if (symbolsToCache.isEmpty()) {
                System.out.println(Context.timeframe + " " + indicator.name() + " already cached");
                return;
            }
            Lists.partition(symbolsToCache, (int) parallelRequests)
                    .forEach(chunk -> {
                        Optional<LoadRequest> existingRequest = requests.stream().filter(request -> request.getType() == CacheType.INDICATOR
                                && request.getTimeframe() == Context.timeframe
                                && request.getIndicator() == indicator
                                && request.getSymbols().containsAll(chunk)).findFirst();
                        if (!existingRequest.isPresent()) {
                            LoadRequest loadRequest = new LoadRequest(CacheType.INDICATOR, Context.timeframe, chunk);
                            loadRequest.setIndicator(indicator);
                            requests.offer(loadRequest);
                        } else {
                            System.out.println("Already in the queue: " + chunk.size() + " " + Context.timeframe.name() + " " + indicator.name());
                        }
                    });
        });
    }

    private static void saveQuote(String symbol, List<QuoteJava> quotes) {
        try {
            String folder = Context.outputFolder + "/cache/" + Context.timeframe.name().toLowerCase();
            File directory = new File(folder);
            if (!directory.exists()) {
                directory.mkdir();
            }
            ZoneId zone = ZoneId.of(myTimezone);
            int currentYear = LocalDateTime.ofInstant(Instant.now(), zone).getYear();
            List<QuoteJava> filteredByHistory = quotes.stream().filter(quote -> {
                int quoteYear = LocalDateTime.ofInstant(Instant.ofEpochSecond(quote.getTimestamp()), zone).getYear();
                return currentYear - quoteYear <= yearsToAnalyze;
            }).collect(Collectors.toList());
            String json = gson.toJson(filteredByHistory);
            Files.write(Paths.get(folder + "/" + symbol + "_quotes.txt"), json.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
