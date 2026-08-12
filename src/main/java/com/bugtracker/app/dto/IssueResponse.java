package com.bugtracker.app.dto;

import com.bugtracker.app.enums.IssueStatus;
import com.bugtracker.app.enums.Priority;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class IssueResponse {
    private Long id;
    private String title;
    private String description;
    private IssueStatus status;
    private Priority priority;
    private String projectName;
    private String reporterName;
    private String assigneeName; // nullable
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}