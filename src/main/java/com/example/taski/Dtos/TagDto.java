package com.example.taski.Dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class TagDto {
    private UUID id;
    private String name;
}