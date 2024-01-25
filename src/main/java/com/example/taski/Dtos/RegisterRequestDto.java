package com.example.taski.Dtos;

import lombok.Data;

@Data
public class RegisterRequestDto {
    private String email = "";
    private String username = "";
    private String fullName = "";
    private String password = "";
    private String confirmPassword = "";
    private String role = "";
}