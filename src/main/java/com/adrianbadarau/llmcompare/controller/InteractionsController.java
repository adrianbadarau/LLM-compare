package com.adrianbadarau.llmcompare.controller;

import com.adrianbadarau.llmcompare.model.DataItem;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/interactions")
public class InteractionsController {

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<List<DataItem>> uploadCSV(
            @RequestParam("file1") MultipartFile file1,
            @RequestParam("file2") MultipartFile file2) throws IOException {

        // Validate files
        if (file1 == null || file1.getSize() <= 0 ||
                file2 == null || file2.getSize() <= 0) {
            return ResponseEntity.badRequest().body(null);
        }

        List<DataItem> result = new ArrayList<>();

        try {
            // Parse first CSV file
            List<DataItem> parsedData1 = parseCSV(file1.getInputStream());
            result.addAll(parsedData1);

            // Parse second CSV file
            List<DataItem> parsedData2 = parseCSV(file2.getInputStream());
            result.addAll(parsedData2);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        return ResponseEntity.ok(result);
    }

    private List<DataItem> parseCSV(InputStream inputStream) throws IOException {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            List<DataItem> records = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            var i = 0;
            for (CSVRecord csvRecord : csvRecords) {
                String data = csvRecord.get("Column1")+ "|" + csvRecord.get("Column2")+ "|" + csvRecord.get("Column3")+"\n";
                var record = new DataItem(data, i++);
                records.add(record);
            }

            return records;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse CSV file: " + e.getMessage());
        }
    }
}