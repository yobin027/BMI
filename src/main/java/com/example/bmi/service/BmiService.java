package com.example.bmi.service;

import com.example.bmi.dto.BmiRequest;
import com.example.bmi.dto.BmiResponse;
import com.example.bmi.model.BmiRecord;
import com.example.bmi.repository.BmiRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BmiService {

    private static final Logger logger = LoggerFactory.getLogger(BmiService.class);

    private final BmiRecordRepository repository;

    @Autowired
    public BmiService(BmiRecordRepository repository) {
        this.repository = repository;
    }

    public BmiResponse calculateAndSave(BmiRequest request) {
        // Convert height to cm
        double heightCm;
        if ("m".equalsIgnoreCase(request.getHeightUnit())) {
            heightCm = request.getHeight() * 100.0;
        } else if ("ft_in".equalsIgnoreCase(request.getHeightUnit())) {
            double inches = (request.getHeight() * 12.0) + (request.getHeightInches() != null ? request.getHeightInches() : 0.0);
            heightCm = inches * 2.54;
        } else {
            heightCm = request.getHeight();
        }

        // Convert weight to kg
        double weightKg;
        if ("lbs".equalsIgnoreCase(request.getWeightUnit())) {
            weightKg = request.getWeight() * 0.45359237;
        } else {
            weightKg = request.getWeight();
        }

        // Round height and weight for display
        heightCm = Math.round(heightCm * 10.0) / 10.0;
        weightKg = Math.round(weightKg * 10.0) / 10.0;

        // BMI calculation: weight (kg) / (height (m) ^ 2)
        double heightM = heightCm / 100.0;
        double rawBmi = weightKg / (heightM * heightM);
        double bmi = Math.round(rawBmi * 100.0) / 100.0;

        // Category & Health Advice
        String category;
        String color;
        String advice;

        if (bmi < 18.5) {
            category = "Underweight";
            color = "#38bdf8"; // Light Blue
            advice = "Your BMI suggests you may be underweight. Consider incorporating nutrient-dense foods, lean proteins, healthy fats, and strength training into your routine.";
        } else if (bmi < 25.0) {
            category = "Normal weight";
            color = "#22c55e"; // Emerald Green
            advice = "Great job! Your BMI is within the healthy range. Maintain your balanced diet, regular exercise, and adequate hydration.";
        } else if (bmi < 30.0) {
            category = "Overweight";
            color = "#f59e0b"; // Amber
            advice = "Your BMI indicates you are slightly above the healthy range. Moderate cardiovascular exercises, portion awareness, and daily walking can help.";
        } else if (bmi < 35.0) {
            category = "Obesity Class I";
            color = "#f97316"; // Orange
            advice = "Your BMI falls in Obesity Class I. Consulting a registered dietitian or healthcare provider can assist in creating a sustainable lifestyle plan.";
        } else if (bmi < 40.0) {
            category = "Obesity Class II";
            color = "#ef4444"; // Red
            advice = "Your BMI falls in Obesity Class II. We recommend discussing lifestyle interventions, routine monitoring, and personalized exercise with a physician.";
        } else {
            category = "Obesity Class III";
            color = "#b91c1c"; // Dark Red
            advice = "Your BMI indicates severe obesity. Medical guidance and structured health management are highly recommended to prevent associated health risks.";
        }

        // Ideal weight range for this height (BMI 18.5 - 24.9)
        double minHealthyWeight = Math.round(18.5 * heightM * heightM * 10.0) / 10.0;
        double maxHealthyWeight = Math.round(24.9 * heightM * heightM * 10.0) / 10.0;

        LocalDateTime now = LocalDateTime.now();

        BmiResponse response = new BmiResponse();
        response.setName(request.getName().trim());
        response.setAge(request.getAge());
        response.setHeightCm(heightCm);
        response.setWeightKg(weightKg);
        response.setBmi(bmi);
        response.setCategory(category);
        response.setCategoryColor(color);
        response.setMinHealthyWeightKg(minHealthyWeight);
        response.setMaxHealthyWeightKg(maxHealthyWeight);
        response.setHealthAdvice(advice);
        response.setCalculatedAt(now);

        // Attempt persistence to MySQL
        try {
            BmiRecord record = new BmiRecord(
                    request.getName().trim(),
                    request.getAge(),
                    heightCm,
                    weightKg,
                    bmi,
                    category,
                    now
            );
            BmiRecord saved = repository.save(record);
            response.setId(saved.getId());
            response.setSavedToDatabase(true);
            response.setDatabaseStatusMessage("Successfully saved to MySQL database (table: bmi_records).");
        } catch (Exception ex) {
            logger.error("Failed to persist BMI record to MySQL: {}", ex.getMessage());
            response.setSavedToDatabase(false);
            response.setDatabaseStatusMessage("Calculated successfully, but MySQL persistence failed (" + ex.getClass().getSimpleName() + "). Please ensure MySQL server is running on localhost:3306.");
        }

        return response;
    }

    public List<BmiRecord> getHistory() {
        try {
            return repository.findAllByOrderByCalculatedAtDesc();
        } catch (Exception ex) {
            logger.warn("Could not query MySQL history: {}", ex.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean deleteRecord(Long id) {
        try {
            if (repository.existsById(id)) {
                repository.deleteById(id);
                return true;
            }
            return false;
        } catch (Exception ex) {
            logger.error("Error deleting BMI record with id {}: {}", id, ex.getMessage());
            return false;
        }
    }

    public boolean isDatabaseConnected() {
        try {
            repository.count();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
