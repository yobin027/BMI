package com.example.bmi.dto;

import java.time.LocalDateTime;

public class BmiResponse {
    private Long id;
    private String name;
    private Integer age;
    private Double heightCm;
    private Double weightKg;
    private Double bmi;
    private String category;
    private String categoryColor;
    private Double minHealthyWeightKg;
    private Double maxHealthyWeightKg;
    private String healthAdvice;
    private boolean savedToDatabase;
    private String databaseStatusMessage;
    private LocalDateTime calculatedAt;

    public BmiResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Double getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(Double heightCm) {
        this.heightCm = heightCm;
    }

    public Double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(Double weightKg) {
        this.weightKg = weightKg;
    }

    public Double getBmi() {
        return bmi;
    }

    public void setBmi(Double bmi) {
        this.bmi = bmi;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryColor() {
        return categoryColor;
    }

    public void setCategoryColor(String categoryColor) {
        this.categoryColor = categoryColor;
    }

    public Double getMinHealthyWeightKg() {
        return minHealthyWeightKg;
    }

    public void setMinHealthyWeightKg(Double minHealthyWeightKg) {
        this.minHealthyWeightKg = minHealthyWeightKg;
    }

    public Double getMaxHealthyWeightKg() {
        return maxHealthyWeightKg;
    }

    public void setMaxHealthyWeightKg(Double maxHealthyWeightKg) {
        this.maxHealthyWeightKg = maxHealthyWeightKg;
    }

    public String getHealthAdvice() {
        return healthAdvice;
    }

    public void setHealthAdvice(String healthAdvice) {
        this.healthAdvice = healthAdvice;
    }

    public boolean isSavedToDatabase() {
        return savedToDatabase;
    }

    public void setSavedToDatabase(boolean savedToDatabase) {
        this.savedToDatabase = savedToDatabase;
    }

    public String getDatabaseStatusMessage() {
        return databaseStatusMessage;
    }

    public void setDatabaseStatusMessage(String databaseStatusMessage) {
        this.databaseStatusMessage = databaseStatusMessage;
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(LocalDateTime calculatedAt) {
        this.calculatedAt = calculatedAt;
    }

    public String getWeightDiffText() {
        if (weightKg == null || minHealthyWeightKg == null || maxHealthyWeightKg == null) {
            return "--";
        }
        if (weightKg < minHealthyWeightKg) {
            double diff = Math.round((minHealthyWeightKg - weightKg) * 10.0) / 10.0;
            return "+" + diff + " kg to healthy";
        } else if (weightKg > maxHealthyWeightKg) {
            double diff = Math.round((weightKg - maxHealthyWeightKg) * 10.0) / 10.0;
            return "-" + diff + " kg to healthy";
        } else {
            return "Optimal range ✓";
        }
    }

    public double getBmiPercentage() {
        if (bmi == null) return 0.0;
        double clamped = Math.max(15.0, Math.min(40.0, bmi));
        return Math.round(((clamped - 15.0) / (40.0 - 15.0)) * 100.0 * 10.0) / 10.0;
    }

    public double getGaugeAngle() {
        if (bmi == null) return -80.0;
        double clamped = Math.max(15.0, Math.min(40.0, bmi));
        double pct = (clamped - 15.0) / (40.0 - 15.0);
        return Math.round((-80.0 + (pct * 160.0)) * 10.0) / 10.0;
    }

    public String getBmiRivRange() {
        if (category == null) return "--";
        switch (category) {
            case "Underweight": return "< 18.5";
            case "Normal weight": return "18.5 – 24.9";
            case "Overweight": return "25.0 – 29.9";
            case "Obesity Class I": return "30.0 – 34.9";
            case "Obesity Class II": return "35.0 – 39.9";
            default: return "≥ 40.0";
        }
    }
}
