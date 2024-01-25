package com.example.taski.Repositories;

import java.util.UUID;
import com.example.taski.Entities.Comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
  
}
