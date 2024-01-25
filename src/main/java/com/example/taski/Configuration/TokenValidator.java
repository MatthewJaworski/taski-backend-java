package com.example.taski.Configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TokenValidator {
  @Value("${jwt.secret-key}")
  private String SECRET_KEY;

  public ResponseEntity<?> validateToken(Map<String, String> headers) {
    String token = headers.get("authorization");
    if (token == null || token.isEmpty()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    final String jwString = token.split(" ")[1].trim();
    try {
      Jwts.parser().setSigningKey(SECRET_KEY.getBytes()).parseClaimsJws(jwString);
    } catch (Exception e) {
      System.out.println("Invalid token: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    return null;
  }

  public String getIdFromToken(Map<String, String> headers) {
    String token = headers.get("authorization");
    if (token == null || token.isEmpty()) {
      return null;
    }

    // Remove the "Bearer " prefix
    if (token.startsWith("Bearer ")) {
      token = token.substring(7);
    }

    try {
      Claims claims = Jwts.parser()
          .setSigningKey(SECRET_KEY.getBytes())
          .parseClaimsJws(token)
          .getBody();

      return claims.get("id", String.class);
    } catch (Exception e) {
      System.out.println("Invalid token: " + e.getMessage());
      return "";
    }
  }
}