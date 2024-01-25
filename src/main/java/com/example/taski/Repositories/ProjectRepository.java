package com.example.taski.Repositories;

import java.util.List;
import java.util.UUID;
import com.example.taski.Entities.Project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
  List<Project> findByUserId(UUID userId);
}