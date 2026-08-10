package com.bugtracker.app.enums;

/**
 * Order here matters conceptually: an issue is expected to move
 * OPEN -> IN_PROGRESS -> RESOLVED -> CLOSED.
 * The allowed-transition rules live in IssueService, not here —
 * this enum just defines the possible states.
 */
public enum IssueStatus {
    OPEN,
    IN_PROGRESS,
    RESOLVED,
    CLOSED
}