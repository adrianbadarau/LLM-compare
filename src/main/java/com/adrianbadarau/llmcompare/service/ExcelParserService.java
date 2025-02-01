package com.adrianbadarau.llmcompare.service;

import com.adrianbadarau.llmcompare.model.DataItem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ExcelService {

    public List<DataItem> parseExcel(InputStream inputStream) throws IOException {
        List<DataItem> records = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0); // Assume first sheet
            Iterator<Row> rowIterator = sheet.iterator();
            int i = 0;

            // Skip header row
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                StringBuilder rowData = new StringBuilder();

                for (Cell cell : row) {
                    rowData.append(getCellValueAsString(cell)).append("|");
                }

                var record = new DataItem(rowData.toString(), i++);
                records.add(record);
            }
        }

        return records;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }
}