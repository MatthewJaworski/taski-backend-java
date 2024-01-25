package com.example.taski.Dtos;

import java.util.UUID;

import lombok.Data;

@Data
public class AddCommentDto {
    private String content;
    private UUID userId;
    private UUID typeId;
}