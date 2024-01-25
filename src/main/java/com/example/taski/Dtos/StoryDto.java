package com.example.taski.Dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import com.example.taski.Entities.Project;
import com.example.taski.Enums.StoryPriority;

@Data
@Getter
@Setter
public class StoryDto {
    private UUID id;
    private UUID projectId;
    private UserDto createdBy;
    private UserDto assignedTo;
    private String name;
    private String description;
    private OffsetDateTime createDate;
    private OffsetDateTime completeDate;
    private boolean isComplete;
    private int storyPoints;
    private StoryPriority priority;
    private TagDto tag;
    private List<CommentDto> comments;

    public StoryDto(UUID id, UUID project, UserDto createdByDto, UserDto assignedToDto, String name,
            String description, OffsetDateTime createDate, OffsetDateTime completeDate, boolean isComplete,
            int storyPoints, StoryPriority priority, TagDto tagDto, List<CommentDto> comments) {
        this.id = id;
        this.projectId = project;
        this.createdBy = createdByDto;
        this.assignedTo = assignedToDto;
        this.name = name;
        this.description = description;
        this.createDate = createDate;
        this.completeDate = completeDate;
        this.isComplete = isComplete;
        this.storyPoints = storyPoints;
        this.priority = priority;
        this.tag = tagDto;
        this.comments = comments;
    }

}