package com.example.taski.Dtos;

import lombok.Data;
import java.util.UUID;
import java.util.List;

@Data
public class CreateProjectDto {
    private UUID userId;
    private String name;

    private String description;
    private List<String> tags;
}