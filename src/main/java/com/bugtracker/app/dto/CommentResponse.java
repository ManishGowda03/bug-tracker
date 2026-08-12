package com.bugtracker.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime createdAt;
}