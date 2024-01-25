package com.example.taski.Repositories;

import java.util.List;
import java.util.UUID;
import com.example.taski.Entities.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public interface StoryRepository extends JpaRepository<Story, UUID> {
  List<Story> findAllByProjectId(UUID projectId);

  List<Story> findAllByAssignedToUser_Id(UUID assignedToUserId, Sort sort);

}