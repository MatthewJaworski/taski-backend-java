package com.example.taski.Dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;
import com.example.taski.Enums.StoryPriority;

@Data
@Getter
@Setter
public class UpdateStoryDto {
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
}