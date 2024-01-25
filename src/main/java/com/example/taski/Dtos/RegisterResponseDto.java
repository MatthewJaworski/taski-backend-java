package com.example.taski.Dtos;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class RegisterResponseDto {
    private String message = "";
    private boolean success;
    private Map<String, List<String>> errors = new HashMap<>();

    public RegisterResponseDto(String message, boolean success, Map<String, List<String>> errors) {
        this.message = message;
        this.success = success;
        this.errors = errors;
    }
}