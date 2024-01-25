package com.example.taski.Extensions;

import java.util.List;
import java.util.stream.Collectors;

import com.example.taski.Dtos.ProjectDto;
import com.example.taski.Dtos.StoryDto;
import com.example.taski.Dtos.UserDto;
import com.example.taski.Entities.Project;

public class ProjectExtensions {

  public static ProjectDto asDto(Project project) {
    if (project == null) {
      return null;
    }

    List<String> tagNames = project.getTagAssociations().stream()
        .filter(ta -> ta != null && ta.getProjectTag() != null)
        .map(ta -> TagExtensions.asDto(ta.getProjectTag()).getName())
        .collect(Collectors.toList());

    List<StoryDto> stories = project.getStories().stream()
        .filter(story -> story != null)
        .map(StoryExtensions::asDto)
        .collect(Collectors.toList());

    List<UserDto> users = project.getUserProjectAssociations().stream()
        .filter(upa -> upa.getUser() != null)
        .map(upa -> UserExtensions.asDto(upa.getUser()))
        .collect(Collectors.toList());

    return new ProjectDto(
        project.getId(),
        project.getUser().getId(),
        project.getName(),
        project.getDescription(),
        project.getCreateDate(),
        tagNames,
        stories,
        users);
  }
}