package com.hrm.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ExcelReader - Data-Driven Testing utility using Apache POI.
 *
 * <p>Reads employee records, test data, and credentials from Excel (.xlsx) files.
 * Supports TestNG @DataProvider integration.
 *
 * @author Deep Ghevariya
 */
public class ExcelReader {

    private static final Logger logger = LogManager.getLogger(ExcelReader.class);

    private ExcelReader() {}

    /**
     * Read all rows from a named sheet as a 2D Object array.
     * Suitable for TestNG @DataProvider.
     *
     * @param filePath  absolute or classpath-relative path to .xlsx file
     * @param sheetName name of the worksheet to read
     * @return Object[][] where each row is an array of cell values (as String)
     */
    public static Object[][] readExcel(String filePath, String sheetName) {
        logger.info("Reading Excel: {} | Sheet: {}", filePath, sheetName);

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new RuntimeException("Sheet '" + sheetName + "' not found in " + filePath);
            }

            int totalRows = sheet.getLastRowNum();
            int totalCols = sheet.getRow(0).getLastCellNum();

            // First row is header — skip it, use remaining rows as data
            Object[][] data = new Object[totalRows][totalCols];

            for (int i = 1; i <= totalRows; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                for (int j = 0; j < totalCols; j++) {
                    Cell cell = row.getCell(j);
                    data[i - 1][j] = getCellValueAsString(cell);
                }
            }

            logger.info("Successfully read {} data rows from sheet '{}'", totalRows, sheetName);
            return data;

        } catch (IOException e) {
            logger.error("Failed to read Excel file: {}", filePath, e);
            throw new RuntimeException("Excel read failure: " + filePath, e);
        }
    }

    /**
     * Read all rows as a List of Maps (column header → cell value).
     * More readable for complex test scenarios.
     *
     * @param filePath  path to .xlsx file
     * @param sheetName worksheet name
     * @return List of row maps
     */
    public static List<Map<String, String>> readExcelAsMapList(String filePath, String sheetName) {
        logger.info("Reading Excel as Map List: {} | Sheet: {}", filePath, sheetName);
        List<Map<String, String>> records = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new RuntimeException("Sheet '" + sheetName + "' not found in " + filePath);
            }

            Row headerRow = sheet.getRow(0);
            int totalCols = headerRow.getLastCellNum();

            // Extract headers
            List<String> headers = new ArrayList<>();
            for (int j = 0; j < totalCols; j++) {
                headers.add(getCellValueAsString(headerRow.getCell(j)));
            }
            logger.debug("Headers found: {}", headers);

            // Extract data rows
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) continue;

                Map<String, String> rowData = new HashMap<>();
                for (int j = 0; j < totalCols; j++) {
                    rowData.put(headers.get(j), getCellValueAsString(row.getCell(j)));
                }
                records.add(rowData);
            }

            logger.info("Loaded {} records from '{}'", records.size(), sheetName);
            return records;

        } catch (IOException e) {
            logger.error("Failed to read Excel: {}", filePath, e);
            throw new RuntimeException("Excel read failure: " + filePath, e);
        }
    }

    /**
     * Get a single cell value by sheet name, row index, and column index.
     *
     * @param filePath  path to .xlsx file
     * @param sheetName worksheet name
     * @param rowIndex  0-indexed row
     * @param colIndex  0-indexed column
     * @return cell value as String
     */
    public static String getCellValue(String filePath, String sheetName, int rowIndex, int colIndex) {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            Row row = sheet.getRow(rowIndex);
            return getCellValueAsString(row.getCell(colIndex));

        } catch (IOException e) {
            logger.error("Failed to get cell value [{}, {}] from {}", rowIndex, colIndex, filePath, e);
            return "";
        }
    }

    // ════════════════════════════════════════════════
    //  PRIVATE HELPERS
    // ════════════════════════════════════════════════

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING  -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue().toString();
                }
                // Remove trailing .0 for whole numbers
                double val = cell.getNumericCellValue();
                yield (val == Math.floor(val)) ? String.valueOf((long) val) : String.valueOf(val);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCachedFormulaResultType() == CellType.NUMERIC
                    ? String.valueOf((long) cell.getNumericCellValue())
                    : cell.getStringCellValue();
            default -> "";
        };
    }

    private static boolean isRowEmpty(Row row) {
        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK
                    && !getCellValueAsString(cell).isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
