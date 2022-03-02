package com.kishlaly.ta.utils;

import com.kishlaly.ta.analyze.TaskResultCode;
import com.kishlaly.ta.model.SymbolData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Log {

    private static StringBuilder log = new StringBuilder();
    private static StringBuilder debug = new StringBuilder();
    private static Map<TaskResultCode, List<SymbolData>> codes = new HashMap<>();

    public static void addLine(String line) {
        log.append(line).append(System.lineSeparator());
    }

    public static void addDebugLine(String line) {
        debug.append(line).append(System.lineSeparator());
    }

    public static void recordCode(TaskResultCode code, SymbolData symbolData) {
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

}
