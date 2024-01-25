package com.example.taski.Entities;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    private UUID id;

    private String fullName;
    private String userName;
    private String email;
    private String normalizedEmail;
    private String passwordHash;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Project> projects = new ArrayList<>();

    @OneToMany(mappedBy = "assignedToUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Story> assignedStories = new ArrayList<>();

    @OneToMany(mappedBy = "createdByUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Story> createdStories = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserProjectAssociation> userProjectAssociations = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserRoleAssociation> userRoles = new ArrayList<>();
}