package com.kishlaly.ta.utils;

import com.kishlaly.ta.analyze.BlockResultCode;
import com.kishlaly.ta.analyze.tasks.blocks.groups.BlocksGroup;
import com.kishlaly.ta.model.SymbolData;

import java.util.*;
import java.util.stream.Collectors;

public class Log {

    private static StringBuilder log = new StringBuilder();
    private static StringBuilder debug = new StringBuilder();
    private static Map<BlockResultCode, List<SymbolData>> codes = new HashMap<>();
    private static Map<Key, Set<String>> summary = new HashMap<>();

    public static void addLine(String line) {
        log.append(line).append(System.lineSeparator());
    }

    public static void addDebugLine(String line) {
        debug.append(line).append(System.lineSeparator());
    }

    public static void recordCode(BlockResultCode code, SymbolData symbolData) {
        List<SymbolData> existingRecord = codes.get(code);
        if (existingRecord == null) {
            existingRecord = new ArrayList<>();
        }
        existingRecord.add(symbolData);
        codes.put(code, existingRecord);
    }

    public static void saveDebug(String filename) {
        FilesUtil.appendToFile(filename, debug.toString());
    }

    public static void saveSummary(String filename) {
        StringBuilder builder = new StringBuilder();
        Map<String, Set<BlocksGroup>> symbolToGroups = new HashMap<>();
        summary.forEach((key, symbols) -> {
            symbols.forEach(symbol -> {
                Set<BlocksGroup> symbolGroups = symbolToGroups.get(symbol);
                if (symbolGroups == null) {
                    symbolGroups = new HashSet<>();
                    symbolToGroups.put(symbol, symbolGroups);
                }
                symbolGroups.add(key.getBlocksGroup());
            });
        });
// придумать как вести лог каждого символа с комментариями как закрылась сделка

        builder.append("<table style=\"border: 1px solid;\">");

        Comparator<Map.Entry<String, Set<BlocksGroup>>> comparator = Comparator.comparingInt(entry -> entry.getValue().size());
        symbolToGroups
                .entrySet()
                .stream()
                .sorted(comparator.reversed())
                .forEach(entry -> {
                    builder.append("<tr style=\"border: 1px solid;\">");
                    builder.append("<td style=\"border: 1px solid; vertical-align: top text-align: left;\">" + entry.getKey() + "</td>");
                    builder.append("<td style=\"border: 1px solid; vertical-align: top; text-align: left;\">");
                    entry.getValue().forEach(group -> {
                        builder.append(group.getClass().getSimpleName() + "<br>");
                        builder.append(group.comments() + "<br><br>");
                    });
                    builder.append("</td>");
                    builder.append("</tr>");
                });
        builder.append("</table>");
        FilesUtil.appendToFile(filename, builder.toString());
    }

    public static void saveSignal(String filename) {
        String output = log.toString();
        if (!output.isEmpty()) {
            FilesUtil.appendToFile(filename, output);
        }
    }

    public static void saveCodes(String folder) {
        codes.forEach((code, symbols) -> {
            String s = symbols.stream().map(symbolData -> symbolData.symbol).collect(Collectors.joining(System.lineSeparator()));
            FilesUtil.appendToFile(folder + "/" + code.name().toLowerCase() + ".txt", s);
        });
    }

    public static void clear() {
        log = new StringBuilder();
        debug = new StringBuilder();
        codes = new HashMap<>();
    }

    public static void addSummary(String name, BlocksGroup blocksGroup, String symbol) {
        Key key = new Key(name, blocksGroup);
        if (summary.get(key) == null) {
            summary.put(key, new HashSet<>());
        }
        summary.get(key).add(symbol);
    }

    public static class Key {
        private String taskName;
        private BlocksGroup blocksGroup;

        public Key(final String taskName, final BlocksGroup blocksGroup) {
            this.taskName = taskName;
            this.blocksGroup = blocksGroup;
        }

        public String getTaskName() {
            return this.taskName;
        }

        public BlocksGroup getBlocksGroup() {
            return this.blocksGroup;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;
            final Key key = (Key) o;
            return this.taskName.equals(key.taskName) && this.blocksGroup.equals(key.blocksGroup);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.taskName, this.blocksGroup);
        }
    }

}
