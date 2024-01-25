package com.example.taski.Configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.lang.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.example.taski.Entities.User;

import java.util.Date;

import java.util.function.Function;

@Component
public class JwtTokenUtil {

  @Value("${jwt.secret-key}")
  private String SECRET_KEY;

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
  }

  private Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  public String generateToken(UserDetails userDetails) {
    JwtTokenGenerator jwtTokenGenerator = new JwtTokenGenerator(SECRET_KEY);
    User user = (User) userDetails;
    return jwtTokenGenerator.generateToken(user);
  }

  public Boolean validateToken(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }

  public boolean validate(String token) {
    try {
      Jwts.parser().setSigningKey(SECRET_KEY.getBytes()).parseClaimsJws(token);
      return true;
    } catch (Exception e) {
      System.out.println(token + "token \n");
      System.out.println(new String(SECRET_KEY.getBytes()) + " SECRET_KEY");
      System.out.println("Token validation failed: " + e.getMessage());
      return false;
    }
  }
}