package com.example.taski.Extensions;

import com.example.taski.Dtos.TagDto;
import com.example.taski.Entities.ITag;
import com.example.taski.Entities.StoryTag;

public class TagExtensions {
  public static TagDto asDto(ITag storyTag) {
    if (storyTag == null) {
      return null;
    }

    TagDto tagDto = new TagDto();
    tagDto.setId(storyTag.getId());
    tagDto.setName(storyTag.getName());

    return tagDto;
  }
}
