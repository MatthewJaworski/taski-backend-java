package com.example.taski.Entities;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import com.example.taski.Enums.StoryPriority;

import java.util.ArrayList;

@Entity
@Getter
@Setter
public class Story {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "projectId")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "createdBy")
    private User createdByUser;

    @ManyToOne
    @JoinColumn(name = "assignedTo")
    private User assignedToUser;

    private String name;
    private String description;
    private OffsetDateTime createDate;
    private OffsetDateTime completeDate;
    private boolean isComplete;
    private int storyPoints;

    @Enumerated(EnumType.STRING)
    private StoryPriority priority;

    @ManyToOne
    @JoinColumn(name = "tagId")
    private StoryTag tag;

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();
}