package com.kishlaly.ta.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.kishlaly.ta.analyze.tasks.blocks.groups.BlocksGroupJava;
import com.kishlaly.ta.model.BlockResultCodeJava;
import com.kishlaly.ta.model.SymbolDataJava;

public class LogJava {

  private static StringBuilder log = new StringBuilder();
  private static StringBuilder debug = new StringBuilder();
  private static Map<BlockResultCodeJava, List<SymbolDataJava>> codes = new HashMap<>();
  private static Map<KeyJava, Set<String>> summary = new HashMap<>();

  public static void addLine(String line) {
    log.append(line).append(System.lineSeparator());
  }

  public static void addDebugLine(String line) {
    debug.append(line).append(System.lineSeparator());
  }

  public static void recordCode(BlockResultCodeJava code, SymbolDataJava symbolData) {
    List<SymbolDataJava> existingRecord = codes.get(code);
    if (existingRecord == null) {
      existingRecord = new ArrayList<>();
    }
    existingRecord.add(symbolData);
    codes.put(code, existingRecord);
  }

  public static void saveDebug(String filename) {
    FileUtilsJava.appendToFile(filename, debug.toString());
  }

  public static void saveSummary(String filename) {
    StringBuilder builder = new StringBuilder();
    Map<String, Set<BlocksGroupJava>> symbolToGroups = new HashMap<>();
    summary.forEach((key, symbols) -> {
      symbols.forEach(symbol -> {
        Set<BlocksGroupJava> symbolGroups = symbolToGroups.get(symbol);
        if (symbolGroups == null) {
          symbolGroups = new HashSet<>();
          symbolToGroups.put(symbol, symbolGroups);
        }
        symbolGroups.add(key.getBlocksGroup());
      });
    });

    builder.append("<table style=\"border: 1px solid;\">");

    Comparator<Map.Entry<String, Set<BlocksGroupJava>>> comparator = Comparator.comparingInt(entry -> entry.getValue()
      .size());
    symbolToGroups
      .entrySet()
      .stream()
      .sorted(comparator.reversed())
      .forEach(entry -> {
        builder.append("<tr style=\"border: 1px solid;\">");
        builder.append("<td style=\"border: 1px solid; vertical-align: top text-align: left;\">" +
          entry.getKey() +
          "</td>");
        builder.append("<td style=\"border: 1px solid; vertical-align: top; text-align: left;\">");
        entry.getValue().forEach(group -> {
          builder.append(group.getClass().getSimpleName() + "<br>");
          builder.append(group.comments() + "<br><br>");
        });
        builder.append("</td>");
        builder.append("</tr>");
      });
    builder.append("</table>");
    FileUtilsJava.appendToFile(filename, builder.toString());

//        if (!symbolToGroups.isEmpty()) {
//            SpreadsheetUtils.createOrUpdateSheet(symbolToGroups);
//        }
  }

  public static void saveSignal(String filename) {
    String output = log.toString();
    if (!output.isEmpty()) {
      FileUtilsJava.appendToFile(filename, output);
    }
  }

  public static void saveCodes(String folder) {
    codes.forEach((code, symbols) -> {
      String s =
        symbols.stream().map(symbolData -> symbolData.symbol).collect(Collectors.joining(System.lineSeparator()));
      FileUtilsJava.appendToFile(folder + "/" + code.name().toLowerCase() + ".txt", s);
    });
  }

  public static void clear() {
    log = new StringBuilder();
    debug = new StringBuilder();
    codes = new HashMap<>();
  }

  public static void addSummary(String name, BlocksGroupJava blocksGroup, String symbol) {
    KeyJava key = new KeyJava(name, blocksGroup);
    if (summary.get(key) == null) {
      summary.put(key, new HashSet<>());
    }
    summary.get(key).add(symbol);
  }

  public static class KeyJava {
    private String taskName;
    private BlocksGroupJava blocksGroup;

    public KeyJava(final String taskName, final BlocksGroupJava blocksGroup) {
      this.taskName = taskName;
      this.blocksGroup = blocksGroup;
    }

    public String getTaskName() {
      return this.taskName;
    }

    public BlocksGroupJava getBlocksGroup() {
      return this.blocksGroup;
    }

    @Override
    public boolean equals(final Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || this.getClass() != o.getClass()) {
        return false;
      }
      final KeyJava key = (KeyJava) o;
      return this.taskName.equals(key.taskName) && this.blocksGroup.equals(key.blocksGroup);
    }

    @Override
    public int hashCode() {
      return Objects.hash(this.taskName, this.blocksGroup);
    }
  }

}
