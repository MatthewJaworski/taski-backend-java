package com.example.taski.Dtos;

import lombok.Data;
import java.util.UUID;
import java.time.OffsetDateTime;

@Data
public class CommentDto {
    private UUID id;
    private String fullName;
    private String content;
    private OffsetDateTime createDate;
}