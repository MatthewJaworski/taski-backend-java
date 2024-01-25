package com.example.taski.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;
import java.util.List;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
public class ProjectDto {
    private UUID id;
    private UUID userId;
    private String name;
    private String description;
    private OffsetDateTime createDate;
    private List<String> tags;
    private List<StoryDto> stories;
    private List<UserDto> users;
}