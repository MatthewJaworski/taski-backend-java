package com.example.taski.Repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.taski.Entities.Role;

public interface RoleRepository extends JpaRepository<Role, UUID>{
  Optional<Role> findByName(String name);
} 