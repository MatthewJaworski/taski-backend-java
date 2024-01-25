package com.example.taski.Repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.taski.Entities.ProjectTag;
import com.example.taski.Entities.ProjectTagAssociation;

public interface ProjectTagRepository extends JpaRepository<ProjectTag, UUID> {
  @Query("select pta from ProjectTagAssociation pta join pta.projectTag pt where pt.name = :name")
  Optional<ProjectTagAssociation> findByTagName(@Param("name") String name);
}