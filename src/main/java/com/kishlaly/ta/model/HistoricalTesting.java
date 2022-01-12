package com.kishlaly.ta.model;

import com.kishlaly.ta.utils.Dates;
import com.kishlaly.ta.utils.Numbers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HistoricalTesting {

    // график с индикаторами
    private SymbolData data;

    // все результаты прогона по стратегии, прокручивая график на один столбик в прошлое
    private List<TaskResult> taskResults;

    // тестирование открытия позиций по полученным сигналам к входу
    private Map<Quote, PositionTestResult> signalTestingResults = new HashMap<>();

    public HistoricalTesting(final SymbolData data, final List<TaskResult> taskResults) {
        this.data = data;
        this.taskResults = taskResults;
    }

    public SymbolData getData() {
        return this.data;
    }

    public void setData(final SymbolData data) {
        this.data = data;
    }

    public List<TaskResult> getTaskResults() {
        return this.taskResults;
    }

    public void setSignals(final List<TaskResult> taskResults) {
        this.taskResults = taskResults;
    }

    public void addTestResult(Quote signal, PositionTestResult positionTestResult) {
        signalTestingResults.put(signal, positionTestResult);
    }

    public PositionTestResult getResult(Quote signal) {
        return signalTestingResults.get(signal);
    }

    public double getSuccessfulRatio() {
        long allPositions = getAllPositionsCount();
        long profitablePositions = getProfitablePositionsCount();
        return Numbers.percent(profitablePositions, allPositions);
    }

    public double getLossRatio() {
        long allPositions = getAllPositionsCount();
        long lossPossitions = getLossPositionsCount();
        return Numbers.percent(lossPossitions, allPositions);
    }

    public long getProfitablePositionsCount() {
        return signalTestingResults.entrySet().stream().filter(entry -> entry.getValue().isProfitable()).count();
    }

    public long getLossPositionsCount() {
        return signalTestingResults.entrySet().stream().filter(entry -> !entry.getValue().isProfitable()).count();
    }

    public long getAllPositionsCount() {
        return signalTestingResults.entrySet().stream().filter(entry -> entry.getValue().isClosed()).count();
    }

    public long getAveragePositionDurationSeconds() {
        return (long) signalTestingResults.entrySet()
                .stream()
                .filter(entry -> entry.getValue().isClosed())
                .mapToLong(entry -> entry.getValue().getPositionDurationInSeconds(data.timeframe)).average().orElse(0);
    }

    public long getMinPositionDurationSeconds() {
        return signalTestingResults.entrySet()
                .stream()
                .filter(entry -> entry.getValue().isClosed())
                .mapToLong(entry -> entry.getValue().getPositionDurationInSeconds(data.timeframe)).min().orElse(0);
    }

    public long getMaxPositionDurationSeconds() {
        return signalTestingResults.entrySet()
                .stream()
                .filter(entry -> entry.getValue().isClosed())
                .mapToLong(entry -> entry.getValue().getPositionDurationInSeconds(data.timeframe)).max().orElse(0);
    }

    public double getMinProfit() {
        return Numbers.round(signalTestingResults.entrySet()
                .stream()
                .filter(entry -> entry.getValue().isProfitable())
                .mapToDouble(entry -> entry.getValue().getProfit()).min().orElse(0));
    }

    public double getMaxProfit() {
        return Numbers.round(signalTestingResults.entrySet()
                .stream()
                .filter(entry -> entry.getValue().isProfitable())
                .mapToDouble(entry -> entry.getValue().getProfit()).max().orElse(0));
    }

    public double getAvgProfit() {
        return Numbers.round(signalTestingResults.entrySet()
                .stream()
                .filter(entry -> entry.getValue().isProfitable())
                .mapToDouble(entry -> entry.getValue().getProfit()).average().orElse(0));
    }

    // find the max of negative number
    public double getMinLoss() {
        return Numbers.round(signalTestingResults.entrySet()
                .stream()
                .filter(entry -> !entry.getValue().isProfitable())
                .mapToDouble(entry -> entry.getValue().getLoss()).max().orElse(0));
    }

    // find the min of negative number
    public double getMaxLoss() {
        return Numbers.round(signalTestingResults.entrySet()
                .stream()
                .filter(entry -> !entry.getValue().isProfitable())
                .mapToDouble(entry -> entry.getValue().getLoss()).min().orElse(0));
    }

    public double getAvgLoss() {
        return Numbers.round(signalTestingResults.entrySet()
                .stream()
                .filter(entry -> !entry.getValue().isProfitable())
                .mapToDouble(entry -> entry.getValue().getLoss()).average().orElse(0));
    }

    public double getTotalProfit() {
        return Numbers.round(signalTestingResults.entrySet()
                .stream()
                .filter(entry -> entry.getValue().isProfitable())
                .mapToDouble(entry -> entry.getValue().getProfit()).sum());
    }

    public double getTotalLoss() {
        return Numbers.round(signalTestingResults.entrySet()
                .stream()
                .filter(entry -> !entry.getValue().isProfitable())
                .mapToDouble(entry -> entry.getValue().getLoss()).sum());
    }

    public PositionTestResult searchSignalByLongestPosition() {
        Optional<Map.Entry<Quote, PositionTestResult>> first = signalTestingResults.entrySet().stream().filter(entrySet -> entrySet.getValue().getPositionDurationInSeconds(data.timeframe) == getMaxPositionDurationSeconds()).findFirst();
        if (first.isPresent()) {
            return first.get().getValue();
        } else {
            return null;
        }
    }

    public PositionTestResult searchSignalByProfit(double value) {
        Optional<Map.Entry<Quote, PositionTestResult>> first = signalTestingResults.entrySet()
                .stream()
                .filter(entrySet -> entrySet.getValue().isProfitable() && entrySet.getValue().getProfit() == value).findFirst();
        if (first.isPresent()) {
            return first.get().getValue();
        } else {
            return null;
        }
    }

    public PositionTestResult searchSignalByLoss(double value) {
        Optional<Map.Entry<Quote, PositionTestResult>> first = signalTestingResults.entrySet()
                .stream()
                .filter(entrySet -> !entrySet.getValue().isProfitable() && entrySet.getValue().getLoss() == value).findFirst();
        if (first.isPresent()) {
            return first.get().getValue();
        } else {
            return null;
        }
    }

    public static class PositionTestResult {

        private long openedTimestamp;
        private long closedTimestamp;

        private double openPositionPrice;
        private double openPositionCost;
        private double closePositionPrice;
        private double closePositionCost;

        private boolean closed;

        private boolean profitable;
        private double profit;
        private double loss;
        private double roi;

        private boolean gapUp;
        private boolean gapDown;

        public boolean isClosed() {
            return this.closed;
        }

        public void setClosed(final boolean closed) {
            this.closed = closed;
        }

        public String getPositionDuration(Timeframe timeframe) {
            if (closed) {
                return Dates.getDuration(timeframe, openedTimestamp, closedTimestamp);
            }
            return "";
        }

        public long getPositionDurationInSeconds(Timeframe timeframe) {
            return closedTimestamp - openedTimestamp;
        }

        public boolean isProfitable() {
            return this.profitable;
        }

        public void setProfitable(final boolean profitable) {
            this.profitable = profitable;
        }

        public double getProfit() {
            return Numbers.round(this.profit);
        }

        public void setProfit(final double profit) {
            this.profit = profit;
        }

        public double getLoss() {
            return Numbers.round(this.loss);
        }

        public void setLoss(final double loss) {
            this.loss = loss;
        }

        public long getOpenedTimestamp() {
            return this.openedTimestamp;
        }

        public void setOpenedTimestamp(final long openedTimestamp) {
            this.openedTimestamp = openedTimestamp;
        }

        public long getClosedTimestamp() {
            return this.closedTimestamp;
        }

        public void setClosedTimestamp(final long closedTimestamp) {
            this.closedTimestamp = closedTimestamp;
        }

        public boolean isGapUp() {
            return this.gapUp;
        }

        public void setGapUp(final boolean gapUp) {
            this.gapUp = gapUp;
        }

        public boolean isGapDown() {
            return this.gapDown;
        }

        public void setGapDown(final boolean gapDown) {
            this.gapDown = gapDown;
        }

        public double getRoi() {
            return this.roi;
        }

        public void setRoi(final double roi) {
            this.roi = roi;
        }

        public double getOpenPositionPrice() {
            return this.openPositionPrice;
        }

        public void setOpenPositionPrice(final double openPositionPrice) {
            this.openPositionPrice = openPositionPrice;
        }

        public double getOpenPositionCost() {
            return this.openPositionCost;
        }

        public void setOpenPositionCost(final double openPositionCost) {
            this.openPositionCost = openPositionCost;
        }

        public double getClosePositionPrice() {
            return this.closePositionPrice;
        }

        public void setClosePositionPrice(final double closePositionPrice) {
            this.closePositionPrice = closePositionPrice;
        }

        public double getClosePositionCost() {
            return this.closePositionCost;
        }

        public void setClosePositionCost(final double closePositionCost) {
            this.closePositionCost = closePositionCost;
        }

    }

}
