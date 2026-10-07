package com.example.bmi.controller;

import com.example.bmi.dto.BmiRequest;
import com.example.bmi.dto.BmiResponse;
import com.example.bmi.model.BmiRecord;
import com.example.bmi.service.BmiService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bmi")
@CrossOrigin(origins = "*")
public class BmiController {

    private final BmiService bmiService;

    @Autowired
    public BmiController(BmiService bmiService) {
        this.bmiService = bmiService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<BmiResponse> calculateBmi(@Valid @RequestBody BmiRequest request) {
        BmiResponse response = bmiService.calculateAndSave(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<BmiRecord>> getHistory() {
        List<BmiRecord> history = bmiService.getHistory();
        return ResponseEntity.ok(history);
    }

    @DeleteMapping("/history/{id}")
    public ResponseEntity<Map<String, Object>> deleteRecord(@PathVariable Long id) {
        boolean deleted = bmiService.deleteRecord(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", deleted);
        result.put("message", deleted ? "Record deleted successfully" : "Record not found or could not be deleted");
        return ResponseEntity.ok(result);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        boolean dbConnected = bmiService.isDatabaseConnected();
        Map<String, Object> status = new HashMap<>();
        status.put("databaseConnected", dbConnected);
        status.put("databaseType", "MySQL");
        status.put("databaseName", "bmi_db");
        status.put("table", "bmi_records");
        status.put("status", dbConnected ? "ONLINE" : "OFFLINE");
        return ResponseEntity.ok(status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
