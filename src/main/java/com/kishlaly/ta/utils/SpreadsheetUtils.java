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
import java.util.ArrayList;
import java.util.List;

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
    }

    private static void appendToSheet(String symbol, List<BlocksGroup> groups) throws Exception {
        FileInputStream file = new FileInputStream(POSITIONS_XLSX);
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

//            CellStyle style = createSymbolCellStyle(workbook);
//            Row row = sheet.createRow(2);

//            Cell cell = row.createCell(0);
//            cell.setCellValue("John Smith");
//            cell.setCellStyle(style);

            File currDir = new File(".");
            String path = currDir.getAbsolutePath();
            String fileLocation = path.substring(0, path.length() - 1) + POSITIONS_XLSX;
            FileOutputStream outputStream = new FileOutputStream(fileLocation);
            positions.write(outputStream);
            positions.close();
        }
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
