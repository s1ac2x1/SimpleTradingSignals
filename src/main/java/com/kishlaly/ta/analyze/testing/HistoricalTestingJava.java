package com.kishlaly.ta.analyze.testing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.kishlaly.ta.analyze.TaskTypeJava;
import com.kishlaly.ta.analyze.tasks.blocks.groups.BlocksGroupJava;
import com.kishlaly.ta.analyze.testing.sl.StopLossStrategyJava;
import com.kishlaly.ta.analyze.testing.tp.TakeProfitStrategyJava;
import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.QuoteJava;
import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.model.TimeframeJava;
import com.kishlaly.ta.utils.DatesJava;
import com.kishlaly.ta.utils.NumbersJava;

public class HistoricalTestingJava {

  private TaskTypeJava taskType;
  private BlocksGroupJava blocksGroup;

  // indicator chart
  private SymbolDataJava data;

  // all results of the strategy run, scrolling the graph one bar back in time
  private List<BlockResultJava> blockResults;

  // testing the opening of positions by the received signals for entry
  private Map<QuoteJava, PositionTestResultJava> signalTestingResults = new HashMap<>();

  private StopLossStrategyJava stopLossStrategy;
  private TakeProfitStrategyJava takeProfitStrategy;

  public HistoricalTestingJava(
    TaskTypeJava taskType,
    BlocksGroupJava blocksGroup,
    final SymbolDataJava data,
    final List<BlockResultJava> blockResults,
    StopLossStrategyJava stopLossStrategy,
    TakeProfitStrategyJava takeProfitStrategy
  ) {
    this.taskType = taskType;
    this.blocksGroup = blocksGroup;
    this.data = data;
    this.blockResults = blockResults;
    this.stopLossStrategy = stopLossStrategy;
    this.takeProfitStrategy = takeProfitStrategy;
  }

  public TaskTypeJava getTaskType() {
    return this.taskType;
  }

  public BlocksGroupJava getBlocksGroup() {
    return this.blocksGroup;
  }

  public StopLossStrategyJava getStopLossStrategy() {
    return this.stopLossStrategy;
  }

  public TakeProfitStrategyJava getTakeProfitStrategy() {
    return this.takeProfitStrategy;
  }

  public SymbolDataJava getData() {
    return this.data;
  }

  public void setData(final SymbolDataJava data) {
    this.data = data;
  }

  public List<BlockResultJava> getTaskResults() {
    return this.blockResults;
  }

  public void setSignals(final List<BlockResultJava> blockResults) {
    this.blockResults = blockResults;
  }

  public void addTestResult(QuoteJava signal, PositionTestResultJava positionTestResult) {
    signalTestingResults.put(signal, positionTestResult);
  }

  public PositionTestResultJava getResult(QuoteJava signal) {
    return signalTestingResults.get(signal);
  }

  public double getSuccessfulRatio() {
    long allPositions = getAllPositionsCount();
    long profitablePositions = getProfitablePositionsCount();
    if (allPositions == 0) {
      return 0;
    }
    return NumbersJava.percent(profitablePositions, allPositions);
  }

  public double getLossRatio() {
    long allPositions = getAllPositionsCount();
    long lossPossitions = getLossPositionsCount();
    if (allPositions == 0) {
      return 0;
    }
    return NumbersJava.percent(lossPossitions, allPositions);
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
    return NumbersJava.round(signalTestingResults.entrySet()
      .stream()
      .filter(entry -> entry.getValue().isProfitable())
      .mapToDouble(entry -> entry.getValue().getProfit()).min().orElse(0));
  }

  public double getMaxProfit() {
    return NumbersJava.round(signalTestingResults.entrySet()
      .stream()
      .filter(entry -> entry.getValue().isProfitable())
      .mapToDouble(entry -> entry.getValue().getProfit()).max().orElse(0));
  }

  public double getAvgProfit() {
    return NumbersJava.round(signalTestingResults.entrySet()
      .stream()
      .filter(entry -> entry.getValue().isProfitable())
      .mapToDouble(entry -> entry.getValue().getProfit()).average().orElse(0));
  }

  // find the max of negative number
  public double getMinLoss() {
    return NumbersJava.round(signalTestingResults.entrySet()
      .stream()
      .filter(entry -> !entry.getValue().isProfitable())
      .mapToDouble(entry -> entry.getValue().getLoss()).max().orElse(0));
  }

  // find the min of negative number
  public double getMaxLoss() {
    return NumbersJava.round(signalTestingResults.entrySet()
      .stream()
      .filter(entry -> !entry.getValue().isProfitable())
      .mapToDouble(entry -> entry.getValue().getLoss()).min().orElse(0));
  }

  public double getAvgLoss() {
    return NumbersJava.round(signalTestingResults.entrySet()
      .stream()
      .filter(entry -> !entry.getValue().isProfitable())
      .mapToDouble(entry -> entry.getValue().getLoss()).average().orElse(0));
  }

  public double getTotalProfit() {
    return NumbersJava.round(signalTestingResults.entrySet()
      .stream()
      .filter(entry -> entry.getValue().isProfitable())
      .mapToDouble(entry -> entry.getValue().getProfit() - entry.getValue().getCommissions()).sum());
  }

  public double getAverageRoi() {
    return NumbersJava.round(signalTestingResults.entrySet()
      .stream()
      .filter(entry -> entry.getValue().isProfitable())
      .mapToDouble(entry -> entry.getValue().getRoi()).average().orElse(0));
  }

  public double getTotalLoss() {
    return NumbersJava.round(signalTestingResults.entrySet()
      .stream()
      .filter(entry -> !entry.getValue().isProfitable())
      .mapToDouble(entry -> entry.getValue().getLoss() - entry.getValue().getCommissions()).sum());
  }

  public PositionTestResultJava searchSignalByLongestPosition() {
    Optional<Map.Entry<QuoteJava, PositionTestResultJava>> first = signalTestingResults.entrySet()
      .stream()
      .filter(entrySet -> entrySet.getValue().getPositionDurationInSeconds(data.timeframe) ==
        getMaxPositionDurationSeconds())
      .findFirst();
    if (first.isPresent()) {
      return first.get().getValue();
    } else {
      return null;
    }
  }

  public PositionTestResultJava searchSignalByLoss(double value) {
    Optional<Map.Entry<QuoteJava, PositionTestResultJava>> first = signalTestingResults.entrySet()
      .stream()
      .filter(entrySet -> !entrySet.getValue().isProfitable() && entrySet.getValue().getLoss() == value).findFirst();
    if (first.isPresent()) {
      return first.get().getValue();
    } else {
      return null;
    }
  }

  public PositionTestResultJava searchSignalByProfit(double value) {
    Optional<Map.Entry<QuoteJava, PositionTestResultJava>> first = signalTestingResults.entrySet()
      .stream()
      .filter(entrySet -> entrySet.getValue().isProfitable() && entrySet.getValue().getProfit() == value).findFirst();
    if (first.isPresent()) {
      return first.get().getValue();
    } else {
      return null;
    }
  }

  public String printSL() {
    return getStopLossStrategy().toString();
  }

  public String printTP() {
    return getTakeProfitStrategy().toString();
  }

  public String printTPSLNumber() {
    return getProfitablePositionsCount() + "/" + getLossPositionsCount();
  }

  public String printTPSLPercent() {
    return getSuccessfulRatio() + "% / " + getLossRatio() + "%";
  }

  public double getBalance() {
    return NumbersJava.round(getTotalProfit() + getTotalLoss()); // loss is negative
  }

  public String getSymbol() {
    return data.symbol;
  }

  public static class PositionTestResultJava {

    private long openedTimestamp;
    private long closedTimestamp;

    private double openPositionPrice;
    private double openPositionCost;
    private double closePositionPrice;
    private double closePositionCost;
    private double commissions;

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

    public String getPositionDuration(TimeframeJava timeframe) {
      if (closed) {
        return DatesJava.getDuration(timeframe, openedTimestamp, closedTimestamp);
      }
      return "";
    }

    public long getPositionDurationInSeconds(TimeframeJava timeframe) {
      return closedTimestamp - openedTimestamp;
    }

    public boolean isProfitable() {
      return this.profitable;
    }

    public void setProfitable(final boolean profitable) {
      this.profitable = profitable;
    }

    public double getProfit() {
      return NumbersJava.round(this.profit);
    }

    public void setProfit(final double profit) {
      this.profit = profit;
    }

    public double getCommissions() {
      return this.commissions;
    }

    public void setCommissions(final double commissions) {
      this.commissions = commissions;
    }

    public double getLoss() {
      return NumbersJava.round(this.loss);
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
