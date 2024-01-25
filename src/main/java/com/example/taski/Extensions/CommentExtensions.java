package com.example.taski.Extensions;

import com.example.taski.Dtos.CommentDto;
import com.example.taski.Entities.Comment;

public class CommentExtensions {

  public static CommentDto asDto(Comment comment) {
    if (comment == null) {
      return null;
    }

    CommentDto commentDto = new CommentDto();
    commentDto.setId(comment.getId());
    commentDto.setFullName(comment.getUser().getFullName());
    commentDto.setContent(comment.getContent());
    commentDto.setCreateDate(comment.getCreateDate());

    return commentDto;
  }
}