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
}
