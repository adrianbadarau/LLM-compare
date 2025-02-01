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
import java.util.HashMap;
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
    public ResponseEntity<List<HashMap<Integer, Integer>>> uploadFiles(
            @RequestParam("file1") MultipartFile file1,
            @RequestParam("file2") MultipartFile file2) {

        // Validate files
        if (file1 == null || file1.getSize() <= 0 ||
                file2 == null || file2.getSize() <= 0) {
            return ResponseEntity.badRequest().body(null);
        }

        try {
            // Parse first file
            List<DataItem> parsedData1 = parseFile(file1);
            // Parse second file
            List<DataItem> parsedData2 = parseFile(file2);
            // Find similarities
            List<HashMap<Integer, Integer>> similarities = llmService.findSimilarItems(parsedData1, parsedData2);

            return ResponseEntity.ok(similarities);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
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