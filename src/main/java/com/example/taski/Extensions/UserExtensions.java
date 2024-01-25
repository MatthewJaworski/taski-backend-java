package com.example.taski.Extensions;

import java.util.stream.Collectors;

import com.example.taski.Dtos.UserDto;
import com.example.taski.Dtos.UserWholeDto;
import com.example.taski.Entities.Story;
import com.example.taski.Entities.User;
import com.example.taski.Entities.Project;

public class UserExtensions {

  public static UserDto asDto(User user) {
    if (user == null) {
      return null;
    }

    UserDto userDto = new UserDto();
    userDto.setId(user.getId());
    userDto.setName(user.getFullName());

    return userDto;
  }

  public static UserWholeDto asWholeDto(User user) {
    if (user == null) {
      return null;
    }

    UserWholeDto userWholeDto = new UserWholeDto();
    userWholeDto.setId(user.getId());
    userWholeDto.setFullName(user.getFullName());
    userWholeDto.setEmail(user.getEmail());
    userWholeDto.setUsername(user.getUserName());
    userWholeDto.setProjects(user.getProjects().stream().map(ProjectExtensions::asDto).collect(Collectors.toList()));
    userWholeDto.setAssignedStories(user.getAssignedStories().stream().map(StoryExtensions::asDto).collect(Collectors.toList()));

    return userWholeDto;
  }
}