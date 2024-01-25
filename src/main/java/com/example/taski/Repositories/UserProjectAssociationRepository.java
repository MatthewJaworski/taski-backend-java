package com.example.taski.Repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.taski.Entities.UserProjectAssociation;

public interface UserProjectAssociationRepository extends JpaRepository<UserProjectAssociation, UUID> {
  List<UserProjectAssociation> findByProjectId(UUID projectId);

  List<UserProjectAssociation> findByUserId(UUID userId);

  Optional<UserProjectAssociation> findByUserIdAndProjectId(UUID userId, UUID projectId);
}
