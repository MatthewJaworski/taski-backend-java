package com.example.taski.Controllers;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.taski.Configuration.TokenValidator;
import com.example.taski.Dtos.AddCommentDto;
import com.example.taski.Entities.Comment;
import com.example.taski.Entities.Story;
import com.example.taski.Entities.User;
import com.example.taski.Repositories.CommentRepository;
import com.example.taski.Repositories.ProjectRepository;
import com.example.taski.Repositories.ProjectTagAssociationRepository;
import com.example.taski.Repositories.ProjectTagRepository;
import com.example.taski.Repositories.StoryRepository;
import com.example.taski.Repositories.UserProjectAssociationRepository;
import com.example.taski.Repositories.UserRepository;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/comments")
@Transactional
public class CommentController {
  @Autowired
  private CommentRepository commentRepository;
  @Autowired
  private StoryRepository storyRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private TokenValidator tokenValidator;

  @PostMapping("/story")
  public ResponseEntity<?> addCommentToStory(@RequestBody AddCommentDto addCommentDto,
      @RequestHeader Map<String, String> headers) {
    ResponseEntity<?> tokenResponse = tokenValidator.validateToken(headers);
    if (tokenResponse != null) {
      return tokenResponse;
    }

    if (addCommentDto.getContent() == null) { 
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("success", false, "message", "Content must not be null"));
  }

    Optional<Story> storyOptional = storyRepository.findById(addCommentDto.getTypeId());
    if (!storyOptional.isPresent()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", "Story not found"));
    }

    Optional<User> userOptional = userRepository.findById(addCommentDto.getUserId());
    if (!userOptional.isPresent()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", "User not found"));
    }

    Comment comment = new Comment();
    comment.setId(UUID.randomUUID());
    comment.setUser(userOptional.get());
    comment.setStory(storyOptional.get());
    comment.setContent(addCommentDto.getContent());
    comment.setCreateDate(OffsetDateTime.now());

    commentRepository.save(comment);

    return ResponseEntity.ok(Map.of("success", true));
  }
}