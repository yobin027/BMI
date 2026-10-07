-- BMI Calculator Database Schema for MySQL
CREATE DATABASE IF NOT EXISTS bmi_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE bmi_db;

CREATE TABLE IF NOT EXISTS bmi_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    height_cm DOUBLE NOT NULL,
    weight_kg DOUBLE NOT NULL,
    bmi DOUBLE NOT NULL,
    category VARCHAR(50) NOT NULL,
    calculated_at DATETIME NOT NULL
);
