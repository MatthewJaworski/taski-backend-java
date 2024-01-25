package com.example.taski.Controllers;

import java.net.URI;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.taski.Configuration.JwtTokenGenerator;
import com.example.taski.Configuration.TokenValidator;
import com.example.taski.Dtos.CreateStoryDto;
import com.example.taski.Dtos.StoryDto;
import com.example.taski.Dtos.UpdateStoryDto;
import com.example.taski.Entities.Project;
import com.example.taski.Entities.Story;
import com.example.taski.Entities.StoryTag;
import com.example.taski.Entities.User;
import com.example.taski.Enums.StoryPriority;
import com.example.taski.Extensions.StoryExtensions;
import com.example.taski.Repositories.ProjectRepository;
import com.example.taski.Repositories.RoleRepository;
import com.example.taski.Repositories.StoryRepository;
import com.example.taski.Repositories.StoryTagRepository;
import com.example.taski.Repositories.UserProjectAssociationRepository;
import com.example.taski.Repositories.UserRepository;
import org.springframework.data.domain.Sort;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/stories")
@Transactional
public class StoryController {

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ProjectRepository projectRepository;
  @Autowired
  private TokenValidator tokenValidator;
  @Autowired
  private StoryTagRepository storyTagRepository;
  @Autowired
  private StoryRepository storyRepository;

  @PostMapping("")
  public ResponseEntity<?> createStory(@RequestBody CreateStoryDto createStoryDto,
      @RequestHeader Map<String, String> headers) {
    ResponseEntity<?> tokenResponse = tokenValidator.validateToken(headers);
    if (tokenResponse != null) {
      return tokenResponse;
    }

    StoryTag tag = storyTagRepository.findByName(createStoryDto.getTag()).orElse(null);

    if (tag == null && createStoryDto.getTag() != null && !createStoryDto.getTag().isEmpty()) {
      tag = new StoryTag();
      tag.setName(createStoryDto.getTag());
      storyTagRepository.save(tag);
    }

    Story story = new Story();
    story.setId(UUID.randomUUID());

    Optional<Project> projectOptional = projectRepository.findById(createStoryDto.getProjectId());
    if (!projectOptional.isPresent()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", "Project not found"));
    }
    story.setProject(projectOptional.get());
    Optional<User> userOptional = userRepository.findById(createStoryDto.getCreatedBy());
    if (!userOptional.isPresent()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", "User not found"));
    }
    if (createStoryDto.getAssignedTo() != null) {
      Optional<User> assignedToOptional = userRepository.findById(createStoryDto.getAssignedTo());

      if (assignedToOptional.isPresent()) {
        story.setAssignedToUser(assignedToOptional.get());
      }

    }

    story.setCreatedByUser(userOptional.get());
    story.setName(createStoryDto.getName());
    story.setDescription(createStoryDto.getDescription());

    story.setCreateDate(Instant.now().atOffset(ZoneOffset.UTC));
    story.setComplete(false);
    story.setPriority(createStoryDto.getPriority() != null ? createStoryDto.getPriority() : StoryPriority.Low);
    story.setStoryPoints(createStoryDto.getStoryPoints() != null ? createStoryDto.getStoryPoints() : 0);

    if (tag != null) {
      story.setTag(tag);
    }

    storyRepository.save(story);

    return ResponseEntity.created(URI.create("/api/stories/" + story.getId()))
        .body(Map.of("success", true, "story", StoryExtensions.asDto(story)));
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updateStory(@PathVariable UUID id, @RequestBody UpdateStoryDto updateStoryDto,
      @RequestHeader Map<String, String> headers) {
    try {

      ResponseEntity<?> tokenResponse = tokenValidator.validateToken(headers);
      if (tokenResponse != null) {
        return tokenResponse;
      }

      Optional<Story> existingStoryOptional = storyRepository.findById(id);
      if (!existingStoryOptional.isPresent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", "Story not found"));
      }

      Story existingStory = existingStoryOptional.get();
      existingStory.setName(updateStoryDto.getName());

      if (updateStoryDto.getCreatedBy() != null
          && !updateStoryDto.getCreatedBy().getId().equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) {
        existingStory.setCreatedByUser(userRepository.findById(updateStoryDto.getCreatedBy().getId()).orElse(null));
      }

      if (updateStoryDto.getAssignedTo() != null
          && !updateStoryDto.getAssignedTo().getId().equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) {
        existingStory.setAssignedToUser(userRepository.findById(updateStoryDto.getAssignedTo().getId()).orElse(null));
      }

      existingStory.setStoryPoints(updateStoryDto.getStoryPoints());
      existingStory.setPriority(updateStoryDto.getPriority());
      existingStory.setComplete(updateStoryDto.isComplete());
      existingStory.setDescription(updateStoryDto.getDescription());

      if (updateStoryDto.getCompleteDate() != null) {
        existingStory.setCompleteDate(updateStoryDto.getCompleteDate());
        existingStory.setComplete(true);
      }

      storyRepository.save(existingStory);
      return ResponseEntity.ok(Map.of("success", true));
    } catch (Exception ex) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("success", false, "message", "An error occurred"));
    }
  }

  @GetMapping("")
  public ResponseEntity<?> getAllStories(
      @RequestHeader Map<String, String> headers) {

    ResponseEntity<?> tokenResponse = tokenValidator.validateToken(headers);
    if (tokenResponse != null) {
      return tokenResponse;
    }

    List<StoryDto> stories = storyRepository.findAll().stream()
        .map(StoryExtensions::asDto)
        .collect(Collectors.toList());
    return ResponseEntity.ok(stories);
  }

  @GetMapping("/project/{projectId}")
  public ResponseEntity<?> getStoriesByProject(@PathVariable UUID projectId,
      @RequestHeader Map<String, String> headers) {

    ResponseEntity<?> tokenResponse = tokenValidator.validateToken(headers);
    if (tokenResponse != null) {
      return tokenResponse;
    }

    List<StoryDto> stories = storyRepository.findAllByProjectId(projectId).stream()
        .map(StoryExtensions::asDto)
        .collect(Collectors.toList());
    return ResponseEntity.ok(stories);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getStory(@PathVariable UUID id,
      @RequestHeader Map<String, String> headers) {

    ResponseEntity<?> tokenResponse = tokenValidator.validateToken(headers);
    if (tokenResponse != null) {
      return tokenResponse;
    }

    Optional<Story> storyOptional = storyRepository.findById(id);
    if (!storyOptional.isPresent()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", "Story not found"));
    }
    StoryDto storyDto = StoryExtensions.asDto(storyOptional.get());
    return ResponseEntity.ok(storyDto);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteStory(@PathVariable UUID id,
      @RequestHeader Map<String, String> headers) {

    ResponseEntity<?> tokenResponse = tokenValidator.validateToken(headers);
    if (tokenResponse != null) {
      return tokenResponse;
    }

    Optional<Story> storyOptional = storyRepository.findById(id);
    if (!storyOptional.isPresent()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", "Story not found"));
    }
    storyRepository.deleteById(id);
    return ResponseEntity.ok(Map.of("success", true));
  }

  @GetMapping("/user/{id}")
  public ResponseEntity<?> getStoriesByUser(@PathVariable UUID id,
      @RequestHeader Map<String, String> headers) {

    ResponseEntity<?> tokenResponse = tokenValidator.validateToken(headers);
    if (tokenResponse != null) {
      return tokenResponse;
    }

    List<StoryDto> stories = storyRepository.findAllByAssignedToUser_Id(id, Sort.by(Sort.Direction.DESC, "createDate"))
        .stream()
        .map(StoryExtensions::asDto)
        .collect(Collectors.toList());
    return ResponseEntity.ok(stories);
  }

}
