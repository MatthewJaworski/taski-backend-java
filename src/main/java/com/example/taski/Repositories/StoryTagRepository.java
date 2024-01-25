package com.example.taski.Repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.example.taski.Entities.StoryTag;

public interface StoryTagRepository extends JpaRepository<StoryTag, UUID> {
  Optional<StoryTag> findByName(@Param("name") String name);
}