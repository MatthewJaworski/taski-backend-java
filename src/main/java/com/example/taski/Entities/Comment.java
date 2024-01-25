package com.example.taski.Entities;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
public class Comment {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    @Column(nullable = false)
    private String content;

    @Column(name = "create_date", nullable = false)
    private OffsetDateTime createDate;
}