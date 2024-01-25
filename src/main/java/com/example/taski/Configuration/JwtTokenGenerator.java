package com.example.taski.Configuration;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import com.example.taski.Entities.User;
import com.example.taski.Entities.UserRoleAssociation;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtTokenGenerator {

  private final String secretKey;


  public JwtTokenGenerator(@Value("${jwt.secret-key}") String secretKey) {
    this.secretKey = secretKey;
  }

  public String generateToken(User user) {
    List<GrantedAuthority> grantedAuthorities = AuthorityUtils
        .commaSeparatedStringToAuthorityList("ROLE_USER");
    UserRoleAssociation roleAssociation = user.getUserRoles().get(0);
    String role = roleAssociation.getRole().getName();

    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.HOUR, 5);
    System.out.println(new String(secretKey.getBytes()) + " SECRET_KEY IN GENERATOR \n");
    System.out.println(user.getId() + " USER IN GENERATOR \n");
    String token = Jwts
        .builder()
        .setId("JWT")
        .setSubject(user.getId().toString())
        .claim("username", user.getUserName())
        .claim("jti", UUID.randomUUID().toString())
        .claim("id", user.getId().toString())
        .claim("email", user.getEmail())
        .claim("fullName", user.getFullName())
        .claim("role", role)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(cal.getTime())
        .setIssuer("https://localhost:5001")
        .setAudience("https://localhost:5001")
        .signWith(SignatureAlgorithm.HS512, secretKey.getBytes())
        .compact();

    return token;
  }

  public String getSecretKey() {
    return this.secretKey;
  }
}