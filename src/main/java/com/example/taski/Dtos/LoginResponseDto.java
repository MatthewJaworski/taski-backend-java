package com.example.taski.Dtos;

import lombok.Data;
import java.util.Map;
import java.util.UUID;
import java.util.HashMap;
import java.util.List;

@Data
public class LoginResponseDto {

  public LoginResponseDto(String token, String message, String email, boolean success, UUID userId) {
    this.accessToken = token;
    this.message = message;
    this.email = email;
    this.success = success;
    this.userId = userId != null ? userId.toString() : "";
  }

  private boolean success;
  private String accessToken = "";
  private String email = "";
  private String userId = "";
  private String message = "";
  private String password = "";
  private Map<String, List<String>> errors = new HashMap<>();
}