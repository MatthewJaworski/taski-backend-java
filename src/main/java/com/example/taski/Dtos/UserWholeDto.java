package com.example.taski.Dtos;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UserWholeDto {
    private UUID id;
    private String fullName;
    private String email;
    private String username;
    private List<ProjectDto> projects;
    private List<StoryDto> assignedStories;
}