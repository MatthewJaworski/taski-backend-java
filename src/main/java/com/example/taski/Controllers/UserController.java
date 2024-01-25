package com.example.taski.Controllers;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.taski.Entities.User;
import com.example.taski.Entities.UserProjectAssociation;
import com.example.taski.Entities.UserRoleAssociation;
import com.example.taski.Extensions.UserExtensions;
import com.example.taski.Entities.Project;
import com.example.taski.Entities.Role;
import com.example.taski.Configuration.JwtTokenGenerator;
import com.example.taski.Configuration.TokenValidator;
import com.example.taski.Dtos.AddUserToProjectDto;
import com.example.taski.Dtos.LoginRequestDto;
import com.example.taski.Dtos.LoginResponseDto;
import com.example.taski.Dtos.RefreshTokenRequestDto;
import com.example.taski.Dtos.RegisterRequestDto;
import com.example.taski.Dtos.RegisterResponseDto;
import com.example.taski.Dtos.UserDto;
import com.example.taski.Dtos.UserWholeDto;
import com.example.taski.Repositories.ProjectRepository;
import com.example.taski.Repositories.RoleRepository;
import com.example.taski.Repositories.UserProjectAssociationRepository;
import com.example.taski.Repositories.UserRepository;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/user")
@Transactional
public class UserController {

  @Autowired
  private RoleRepository roleRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private ProjectRepository projectRepository;
  @Autowired
  private JwtTokenGenerator jwtTokenGenerator;
  @Autowired
  private TokenValidator tokenValidator;
  @Autowired
  private UserProjectAssociationRepository userProjectAssociationRepository;

  @PostMapping("/register")
  public ResponseEntity<RegisterResponseDto> register(@RequestBody RegisterRequestDto request) {
    try {

      Map<String, List<String>> errors = new HashMap<>();

      // Email validation
      String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
      Pattern pattern = Pattern.compile(emailRegex);
      Matcher matcher = pattern.matcher(request.getEmail());
      if (!matcher.matches()) {
        errors.put("Email", Collections.singletonList("Email format is not valid"));
      }

      // Username validation
      String usernameRegex = "^[a-zA-Z0-9]*$";
      pattern = Pattern.compile(usernameRegex);
      matcher = pattern.matcher(request.getUsername());
      if (!matcher.matches()) {
        errors.put("Username", Collections.singletonList("Username can only contain alphanumeric characters"));
      }

      // Password validation
      String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\da-zA-Z]).*$";
      pattern = Pattern.compile(passwordRegex);
      matcher = pattern.matcher(request.getPassword());
      if (request.getPassword().length() < 8 || !matcher.matches()) {
        errors.put("Password", Collections.singletonList(
            "Password must be at least 8 characters and contain at least one lowercase letter, one uppercase letter, one digit, and one special character"));
      }

      if (!request.getPassword().equals(request.getConfirmPassword())) {
        errors.put("Password", Collections.singletonList("Confirm password must match password"));
      }

      if (!errors.isEmpty()) {
        return ResponseEntity.ok().body(new RegisterResponseDto("User creation failed", false, errors));
      }

      Optional<User> userExists = userRepository.findByEmail(request.getEmail());
      if (userExists.isPresent()) {
        errors.put("Email", Collections.singletonList("Email already exists"));
        return ResponseEntity.badRequest().body(new RegisterResponseDto("User creation failed", false, errors));
      }
      User newUser = new User();
      newUser.setFullName(request.getFullName());
      newUser.setEmail(request.getEmail());
      newUser.setUserName(request.getUsername());
      newUser.setNormalizedEmail(request.getEmail().toUpperCase());
      newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));

      Optional<Role> role = roleRepository.findByName(request.getRole());
      if (!role.isPresent()) {
        Role newRole = new Role();
        newRole.setName(request.getRole());
        role = Optional.of(roleRepository.save(newRole));
      }

      UserRoleAssociation userRoleAssociation = new UserRoleAssociation();
      userRoleAssociation.setUser(newUser);
      userRoleAssociation.setRole(role.get());

      newUser.getUserRoles().add(userRoleAssociation);

      userRepository.save(newUser);

      return ResponseEntity.ok(new RegisterResponseDto("User created successfully", true, null));

    } catch (Exception e) {
      Map<String, List<String>> errors = new HashMap<>();
      errors.put("Exception", Collections.singletonList(e.getMessage()));
      return ResponseEntity.badRequest().body(new RegisterResponseDto("User creation failed", false, errors));
    }

  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
    try {
      Map<String, List<String>> errors = new HashMap<>();

      Optional<User> user = userRepository.findByEmail(request.getEmail());
      System.out.println(user);
      if (!user.isPresent()) {
        errors.put("General", Collections.singletonList("Invalid email/password"));
        return ResponseEntity.ok().body(new LoginResponseDto(null, "Invalid email or password", null, false, null));
      }

      boolean isPasswordValid = passwordEncoder.matches(request.getPassword(), user.get().getPasswordHash());

      if (!isPasswordValid) {
        errors.put("General", Collections.singletonList("Invalid email/password"));
        return ResponseEntity.ok().body(new LoginResponseDto(null, "Invalid email or password", null, false, null));
      }

      String token = jwtTokenGenerator.generateToken(user.get());

      return ResponseEntity.ok().body(new LoginResponseDto(token, "Login successful", user.get().getEmail(), true,
          user.get().getId()));

    } catch (Exception ex) {
      return ResponseEntity.badRequest().body(new LoginResponseDto(null, ex.getMessage(), null, false, null));
    }
  }

  @GetMapping("")
  public ResponseEntity<?> getAllUsers(@RequestHeader Map<String, String> headers) {
    try {
      ResponseEntity<?> tokenResponse = tokenValidator.validateToken(headers);
      if (tokenResponse != null) {
        return tokenResponse;
      }

      List<UserDto> users = userRepository.findAll().stream()
          .map(UserExtensions::asDto)
          .collect(Collectors.toList());
      return ResponseEntity.ok(users);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      return ResponseEntity.badRequest().body(null);
    }
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequestDto request,
      @RequestHeader Map<String, String> headers) {
    try {
      ResponseEntity<?> tokenResponse = tokenValidator.validateToken(headers);
      if (tokenResponse != null) {
        return tokenResponse;
      }
      String id = tokenValidator.getIdFromToken(headers);

      Optional<User> userOptional = null;
      if (id != null) {
        userOptional = userRepository.findById(UUID.fromString(id));
      }
      if (!userOptional.isPresent()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }

      User user = userOptional.get();
      String refreshedToken = jwtTokenGenerator.generateToken(user);
      return ResponseEntity.ok(Collections.singletonMap("refreshedToken", refreshedToken));
    } catch (Exception ex) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getUser(@PathVariable UUID id, @RequestHeader Map<String, String> headers) {
    ResponseEntity<?> tokenResponse = tokenValidator.validateToken(headers);
    if (tokenResponse != null) {
      return tokenResponse;
    }
    Optional<User> userOptional = userRepository.findById(id);
    if (!userOptional.isPresent()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", "User not found"));
    }

    User user = userOptional.get();

    UserWholeDto userWholeDto = UserExtensions.asWholeDto(user);

    return ResponseEntity.ok(userWholeDto);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteUser(@PathVariable UUID id, @RequestHeader Map<String, String> headers) {
    ResponseEntity<?> tokenResponse = tokenValidator.validateToken(headers);
    if (tokenResponse != null) {
      return tokenResponse;
    }

    Optional<User> userOptional = userRepository.findById(id);
    if (!userOptional.isPresent()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", "User not found"));
    }

    User user = userOptional.get();
    List<Project> userProjects = projectRepository.findByUserId(id);

    for (Project project : userProjects) {
      projectRepository.delete(project);
    }

    userRepository.delete(user);

    return ResponseEntity.ok(Map.of("success", true, "message", "User deleted successfully"));
  }

  @PostMapping("/project")
  public ResponseEntity<?> addUserToProject(@RequestBody AddUserToProjectDto addUserToProjectDto,
      @RequestHeader Map<String, String> headers) {
    ResponseEntity<?> tokenResponse = tokenValidator.validateToken(headers);
    if (tokenResponse != null) {
      return tokenResponse;
    }
    Optional<User> userOptional = userRepository.findById(addUserToProjectDto.getUserId());
    if (!userOptional.isPresent()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", "User not found"));
    }

    Optional<Project> projectOptional = projectRepository.findById(addUserToProjectDto.getProjectId());
    if (!projectOptional.isPresent()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", "Project not found"));
    }

    User user = userOptional.get();
    Project project = projectOptional.get();

    Optional<UserProjectAssociation> existingAssociationOptional = userProjectAssociationRepository
        .findByUserIdAndProjectId(user.getId(), project.getId());
    if (existingAssociationOptional.isPresent()) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
          .body(Map.of("success", false, "message", "User is already in the project"));
    }

    UserProjectAssociation userProject = new UserProjectAssociation();
    userProject.setUser(user);
    userProject.setProject(project);

    userProjectAssociationRepository.save(userProject);

    return ResponseEntity.ok(Map.of("success", true, "message", "User added to project successfully"));
  }

  @DeleteMapping("/project")
  public ResponseEntity<?> removeUserFromProject(@RequestBody AddUserToProjectDto addUserToProjectDto,
      @RequestHeader Map<String, String> headers) {
    ResponseEntity<?> tokenResponse = tokenValidator.validateToken(headers);
    if (tokenResponse != null) {
      return tokenResponse;
    }
    Optional<User> userOptional = userRepository.findById(addUserToProjectDto.getUserId());
    if (!userOptional.isPresent()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", "User not found"));
    }

    Optional<Project> projectOptional = projectRepository.findById(addUserToProjectDto.getProjectId());
    if (!projectOptional.isPresent()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", "Project not found"));
    }

    User user = userOptional.get();
    Project project = projectOptional.get();

    Optional<UserProjectAssociation> existingAssociationOptional = userProjectAssociationRepository
        .findByUserIdAndProjectId(user.getId(), project.getId());
    if (!existingAssociationOptional.isPresent()) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
          .body(Map.of("success", false, "message", "User is not in the project"));
    }

    userProjectAssociationRepository.delete(existingAssociationOptional.get());

    return ResponseEntity.ok(Map.of("success", true, "message", "User removed from project successfully"));
  }
}