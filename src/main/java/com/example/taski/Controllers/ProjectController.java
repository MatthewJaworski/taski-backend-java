package com.example.taski.Controllers;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.taski.Configuration.TokenValidator;
import com.example.taski.Dtos.CreateProjectDto;
import com.example.taski.Dtos.ProjectDto;
import com.example.taski.Dtos.UpdateProjectDto;
import com.example.taski.Dtos.UserDto;
import com.example.taski.Entities.Project;
import com.example.taski.Entities.ProjectTag;
import com.example.taski.Entities.ProjectTagAssociation;
import com.example.taski.Entities.Story;
import com.example.taski.Entities.User;
import com.example.taski.Entities.UserProjectAssociation;
import com.example.taski.Extensions.ProjectExtensions;
import com.example.taski.Extensions.UserExtensions;
import com.example.taski.Repositories.ProjectRepository;
import com.example.taski.Repositories.ProjectTagAssociationRepository;
import com.example.taski.Repositories.ProjectTagRepository;
import com.example.taski.Repositories.StoryRepository;
import com.example.taski.Repositories.UserProjectAssociationRepository;
import com.example.taski.Repositories.UserRepository;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/projects")
@Transactional
public class ProjectController {
  @Autowired
  private ProjectRepository projectRepository;
  @Autowired
  private ProjectTagRepository tagRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ProjectTagAssociationRepository tagAssociationRepository;
  @Autowired
  private UserProjectAssociationRepository userProjectAssociationRepository;
  @Autowired
  private StoryRepository storyRepository;
  @Autowired
  private TokenValidator tokenValidator;

  @PostMapping("")
  public ResponseEntity<?> createProject(@RequestBody CreateProjectDto createProjectDto,
      @RequestHeader Map<String, String> headers) {
    ResponseEntity<?> tokenResponse = tokenValidator.validateToken(headers);
    if (tokenResponse != null) {
      return tokenResponse;
    }

    Project project = new Project();
    project.setId(UUID.randomUUID());
    project.setName(createProjectDto.getName());
    project.setDescription(createProjectDto.getDescription());
    project.setCreateDate(Instant.now().atOffset(ZoneOffset.UTC));

    Optional<User> user = userRepository.findById(createProjectDto.getUserId());

    project.setUser(user.get());

    projectRepository.save(project);

    for (String tagName : createProjectDto.getTags()) {
      Optional<ProjectTagAssociation> tagOptional = tagRepository.findByTagName(tagName);
      ProjectTag tag;
      if (!tagOptional.isPresent()) {
        tag = new ProjectTag();
        tag.setName(tagName);
        tagRepository.save(tag);
      } else {
        ProjectTagAssociation tagAssociation = tagOptional.get();
        tag = tagAssociation.getProjectTag();
      }

      ProjectTagAssociation tagAssociation = new ProjectTagAssociation();
      tagAssociation.setProject(project);
      tagAssociation.setProjectTag(tag);

      tagAssociationRepository.save(tagAssociation);
    }

    UserProjectAssociation userProjectAssociationEntity = new UserProjectAssociation();

    userProjectAssociationEntity.setUser(project.getUser());
    userProjectAssociationEntity.setProject(project);

    userProjectAssociationRepository.save(userProjectAssociationEntity);

    return ResponseEntity.ok(Map.of("success", true, "project", ProjectExtensions.asDto(project)));
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updateProject(@PathVariable UUID id, @RequestBody UpdateProjectDto updateProjectDto,
      @RequestHeader Map<String, String> headers) {
    ResponseEntity<?> tokenResponse = tokenValidator.validateToken(headers);
    if (tokenResponse != null) {
      return tokenResponse;
    }

    Optional<Project> existingProjectOptional = projectRepository.findById(id);
    if (!existingProjectOptional.isPresent()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    Project existingProject = existingProjectOptional.get();
    existingProject.setName(updateProjectDto.getName());
    existingProject.setDescription(updateProjectDto.getDescription());
    projectRepository.save(existingProject);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteProject(@PathVariable UUID id, @RequestHeader Map<String, String> headers) {
    ResponseEntity<?> tokenResponse = tokenValidator.validateToken(headers);
    if (tokenResponse != null) {
      return tokenResponse;
    }

    Optional<Project> existingProjectOptional = projectRepository.findById(id);
    if (!existingProjectOptional.isPresent()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    List<Story> stories = storyRepository.findAll();
    for (Story story : stories) {
      storyRepository.delete(story);
    }
    projectRepository.deleteById(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @GetMapping("/{projectId}/users")
  public ResponseEntity<?> getUsersByProjectId(@PathVariable UUID projectId,
      @RequestHeader Map<String, String> headers) {
    ResponseEntity<?> tokenResponse = tokenValidator.validateToken(headers);
    if (tokenResponse != null) {
      return tokenResponse;
    }
    List<UserProjectAssociation> userProjectAssociations = userProjectAssociationRepository.findByProjectId(projectId);
    List<UserDto> users = new ArrayList<>();
    for (UserProjectAssociation upa : userProjectAssociations) {
      User user = upa.getUser();
      if (user != null) {
        UserDto userDto = UserExtensions.asDto(user);
        users.add(userDto);
      }
    }
    return new ResponseEntity<>(users, HttpStatus.OK);
  }

  @GetMapping("")
  public ResponseEntity<?> getAllProjects(@RequestHeader Map<String, String> headers) {
    ResponseEntity<?> tokenResponse = tokenValidator.validateToken(headers);
    if (tokenResponse != null) {
      return tokenResponse;
    }
    List<Project> projects = projectRepository.findAll();
    List<ProjectDto> projectDtos = projects.stream()
        .map(ProjectExtensions::asDto)
        .collect(Collectors.toList());
    return new ResponseEntity<>(Collections.singletonMap("projects", projectDtos), HttpStatus.OK);
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<?> getProjectsByUserId(@PathVariable UUID userId, @RequestHeader Map<String, String> headers) {
    ResponseEntity<?> tokenResponse = tokenValidator.validateToken(headers);
    if (tokenResponse != null) {
      return tokenResponse;
    }

    List<UserProjectAssociation> userProjectAssociations = userProjectAssociationRepository.findByUserId(userId);
    List<UUID> projectIds = userProjectAssociations.stream()
        .map(upa -> upa.getProject().getId())
        .collect(Collectors.toList());

    List<Project> projects = projectRepository.findAllById(projectIds);
    projects.sort(Comparator.comparing(Project::getCreateDate).reversed());

    List<ProjectDto> projectDtos = projects.stream()
        .map(ProjectExtensions::asDto)
        .collect(Collectors.toList());

        return new ResponseEntity<>(Collections.singletonMap("projects", projectDtos), HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getProjectById(@PathVariable UUID id, @RequestHeader Map<String, String> headers) {
    ResponseEntity<?> tokenResponse = tokenValidator.validateToken(headers);
    if (tokenResponse != null) {
      return tokenResponse;
    }
    Optional<Project> optionalProject = projectRepository.findById(id);
    if (!optionalProject.isPresent()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    Project project = optionalProject.get();
    ProjectDto projectDto = ProjectExtensions.asDto(project);

    return new ResponseEntity<>(projectDto, HttpStatus.OK);
  }
}
