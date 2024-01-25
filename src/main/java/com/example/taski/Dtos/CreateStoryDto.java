package com.example.taski.Dtos;

import lombok.Data;
import java.util.UUID;
import com.example.taski.Enums.StoryPriority;

@Data
public class CreateStoryDto {
    private UUID projectId;
    private UUID createdBy;
    private String name;
    private UUID assignedTo;
    private String description;
    private Integer storyPoints;
    private StoryPriority priority;
    private String tag;
}