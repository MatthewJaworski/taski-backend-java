package com.example.taski.Repositories;

import java.util.Optional;
import java.util.UUID;
import com.example.taski.Entities.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
  
  Optional<User> findByEmail(String email);
}