package com.kishlaly.ta.utils;

import com.kishlaly.ta.analyze.tasks.blocks.groups.BlocksGroup;
import com.kishlaly.ta.analyze.tasks.blocks.groups.ThreeDisplays_Buy_2;
import com.kishlaly.ta.analyze.tasks.blocks.groups.ThreeDisplays_Buy_4;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SpreadsheetUtils {

    public static final String POSITIONS_XLSX = "positions.xlsx";
    private static Workbook positions;

    public static void main(String[] args) throws Exception {
        createSheetIfNotExists();
        String symbol = "AAPL";
        List<BlocksGroup> groups = new ArrayList<>();
        groups.add(new ThreeDisplays_Buy_2());
        groups.add(new ThreeDisplays_Buy_4());
        appendToSheet(symbol, groups);
        saveSheet();
    }

    public static void appendToSheet(String symbol, List<BlocksGroup> groups) throws Exception {
        if (positions == null) {
            createSheetIfNotExists();
        }
        if (hasOpenedPositionsForSymbol(symbol)) {
            return;
        }
        Sheet sheet = positions.getSheetAt(0);
        Row newRow = sheet.createRow(sheet.getLastRowNum() + 1);
        String groupsOutput = groups.stream().map(group -> group.getClass().getSimpleName() + System.lineSeparator() + group.comments()).collect(Collectors.joining(System.lineSeparator() + System.lineSeparator()));
        addRowCell(newRow, symbol, 0);
        addRowCell(newRow, groupsOutput, 1);
        addRowCell(newRow, "", 2);
    }

    private static boolean hasOpenedPositionsForSymbol(String symbol) {
        Sheet sheet = positions.getSheetAt(0);
        for (Row row : sheet) {
            Cell symbolCell = row.getCell(0);
            String symbolCellValue = symbolCell.getRichStringCellValue().getString();
            if (symbolCellValue.trim().toLowerCase().contains(symbol.trim().toLowerCase())) {
                Cell resultCell = row.getCell(2);
                String resultCellValue = resultCell.getRichStringCellValue().getString().trim();
                if (resultCellValue.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void addRowCell(Row row, String value, int cellIndex) {
        CellStyle cellStyle = positions.createCellStyle();
        cellStyle.setFont(createSymbolCellFont((XSSFWorkbook) positions));
        cellStyle.setWrapText(true);
        cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
        Cell cell = row.createCell(cellIndex);
        cell.setCellValue(value);
        cell.setCellStyle(cellStyle);
    }

    private static void createSheetIfNotExists() throws Exception {
        File file = new File(POSITIONS_XLSX);
        if (!file.exists()) {
            positions = new XSSFWorkbook();
            Sheet sheet = positions.createSheet("Positions");
            sheet.setColumnWidth(0, 6000);
            sheet.setColumnWidth(1, 4000);

            Row header = sheet.createRow(0);
            CellStyle headerStyle = positions.createCellStyle();
            headerStyle.setFont(createHeaderCellFont((XSSFWorkbook) positions));
            headerStyle.setWrapText(true);

            Cell symbol = header.createCell(0);
            symbol.setCellValue("Symbol");
            symbol.setCellStyle(headerStyle);

            Cell signals = header.createCell(1);
            signals.setCellValue("Signals");
            signals.setCellStyle(headerStyle);

            Cell result = header.createCell(2);
            result.setCellValue("Result");
            result.setCellStyle(headerStyle);

            Cell comments = header.createCell(3);
            comments.setCellValue("Comments");
            comments.setCellStyle(headerStyle);

            saveSheet();
        } else {
            FileInputStream sheetFile = new FileInputStream(POSITIONS_XLSX);
            positions = new XSSFWorkbook(sheetFile);
        }
    }

    private static void saveSheet() throws IOException {
        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() - 1) + POSITIONS_XLSX;
        FileOutputStream outputStream = new FileOutputStream(fileLocation);
        positions.write(outputStream);
    }

    @NotNull
    private static CellStyle createSymbolCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);
        return style;
    }

    private static XSSFFont createHeaderCellFont(XSSFWorkbook workbook) {
        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 16);
        font.setBold(true);
        return font;
    }

    private static XSSFFont createSymbolCellFont(XSSFWorkbook workbook) {
        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 15);
        return font;
    }

    private static XSSFFont createRegularCellFont(XSSFWorkbook workbook) {
        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 14);
        return font;
    }

}
