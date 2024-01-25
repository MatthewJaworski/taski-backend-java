package com.example.taski.Extensions;

import java.util.List;
import java.util.stream.Collectors;

import com.example.taski.Dtos.CommentDto;
import com.example.taski.Dtos.StoryDto;
import com.example.taski.Dtos.TagDto;
import com.example.taski.Dtos.UserDto;
import com.example.taski.Entities.Story;

public class StoryExtensions {

  public static StoryDto asDto(Story story) {
    if (story == null) {
      return null;
    }

    TagDto tagDto = null;
    if (story.getTag() != null) {
      tagDto = TagExtensions.asDto(story.getTag());
    }

    UserDto createdByDto = story.getCreatedByUser() != null ? UserExtensions.asDto(story.getCreatedByUser()) : null;
    UserDto assignedToDto = story.getAssignedToUser() != null ? UserExtensions.asDto(story.getAssignedToUser()) : null;
    List<CommentDto> comments = story.getComments().stream().map(CommentExtensions::asDto).collect(Collectors.toList());

    return new StoryDto(
        story.getId(),
        story.getProject().getId(),
        createdByDto,
        assignedToDto,
        story.getName(),
        story.getDescription(),
        story.getCreateDate(),
        story.getCompleteDate(),
        story.isComplete(),
        story.getStoryPoints(),
        story.getPriority(),
        tagDto,
        comments);
  }
}