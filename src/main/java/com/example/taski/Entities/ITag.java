package com.example.taski.Entities;

import java.util.UUID;

public interface ITag {
    UUID getId();
    void setId(UUID id);
    String getName();
    void setName(String name);
}