package com.example.bmi.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class BmiRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Age is required")
    @Min(value = 1, message = "Age must be at least 1")
    @Max(value = 130, message = "Age must be realistic (under 130)")
    private Integer age;

    @NotNull(message = "Height is required")
    @Positive(message = "Height must be positive")
    private Double height;

    // Optional unit: "cm" (default), "m", "ft_in"
    private String heightUnit = "cm";

    // Used if heightUnit is "ft_in"
    private Double heightInches = 0.0;

    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    private Double weight;

    // Optional unit: "kg" (default), "lbs"
    private String weightUnit = "kg";

    public BmiRequest() {
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

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public String getHeightUnit() {
        return heightUnit;
    }

    public void setHeightUnit(String heightUnit) {
        this.heightUnit = heightUnit;
    }

    public Double getHeightInches() {
        return heightInches;
    }

    public void setHeightInches(Double heightInches) {
        this.heightInches = heightInches;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(String weightUnit) {
        this.weightUnit = weightUnit;
    }
}
