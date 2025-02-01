package com.adrianbadarau.llmcompare.controller;

import com.adrianbadarau.llmcompare.model.DataItem;
import com.adrianbadarau.llmcompare.service.CSVService;
import com.adrianbadarau.llmcompare.service.ExcelService;
import com.adrianbadarau.llmcompare.service.LLMService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/interactions")
public class InteractionsController {

    private final CSVService csvService;
    private final ExcelService excelService;
    private final LLMService llmService;

    public InteractionsController(CSVService csvService, ExcelService excelService, LLMService llmService) {
        this.csvService = csvService;
        this.excelService = excelService;
        this.llmService = llmService;
    }

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadFiles(
            @RequestParam("file1") MultipartFile file1,
            @RequestParam("file2") MultipartFile file2) {

        // Validate files
        if (file1 == null || file1.getSize() <= 0 ||
                file2 == null || file2.getSize() <= 0) {
            return ResponseEntity.badRequest().body(null);
        }

        List<DataItem> result = new ArrayList<>();

        try {
            // Parse first file
            List<DataItem> parsedData1 = parseFile(file1);
            result.addAll(parsedData1);

            // Parse second file
            List<DataItem> parsedData2 = parseFile(file2);
            result.addAll(parsedData2);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        String comparisonResult = llmService.compareItems(result.getFirst(), result.getLast());

        return ResponseEntity.ok(comparisonResult);
    }

    private List<DataItem> parseFile(MultipartFile file) throws IOException {
        if (file.getOriginalFilename().endsWith(".csv")) {
            return csvService.parseCSV(file.getInputStream());
        } else if (file.getOriginalFilename().endsWith(".xls") || file.getOriginalFilename().endsWith(".xlsx")) {
            return excelService.parseExcel(file.getInputStream());
        } else {
            throw new IllegalArgumentException("Unsupported file format: " + file.getOriginalFilename());
        }
    }
}