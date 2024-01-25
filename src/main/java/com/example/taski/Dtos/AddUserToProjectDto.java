package com.example.taski.Dtos;

import lombok.Data;
import java.util.UUID;

@Data
public class AddUserToProjectDto {
    private UUID projectId;
    private UUID userId;
}