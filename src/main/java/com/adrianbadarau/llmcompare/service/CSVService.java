package com.adrianbadarau.llmcompare.service;

import com.adrianbadarau.llmcompare.model.DataItem;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class CSVService {

    public List<DataItem> parseCSV(InputStream inputStream) throws IOException {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            List<DataItem> records = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            var i = 0;
            for (CSVRecord csvRecord : csvRecords) {
                String data = csvRecord.get("Column1") + "|" + csvRecord.get("Column2") + "|" + csvRecord.get("Column3") + "\n";
                var record = new DataItem(data, i++);
                records.add(record);
            }

            return records;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse CSV file: " + e.getMessage());
        }
    }
}