package com.kishlaly.ta.utils;

import com.kishlaly.ta.analyze.BlockResultCode;
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
        summary.forEach((key, symbols) -> {
            builder.append(key.getTaskName() + " - " + key.getBlockName());
        });
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

    public static void add(Key key, String symbol) {
        if (summary.get(key) == null) {
            summary.put(key, new HashSet<>());
        }
        summary.get(key).add(symbol);
    }

    public static class Key {
        private String taskName;
        private String blockName;

        public Key(final String taskName, final String blockName) {
            this.taskName = taskName;
            this.blockName = blockName;
        }

        public String getTaskName() {
            return this.taskName;
        }

        public String getBlockName() {
            return this.blockName;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;
            final Key key = (Key) o;
            return this.taskName.equals(key.taskName) && this.blockName.equals(key.blockName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.taskName, this.blockName);
        }
    }

}
