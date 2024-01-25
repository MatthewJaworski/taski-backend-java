package com.example.taski.Entities;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;


import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

@Entity
@Getter
@Setter
public class ProjectTag implements ITag {
    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    @OneToMany(mappedBy = "projectTag", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectTagAssociation> projectAssociations = new ArrayList<>();
}