package com.bugtracker.app.controller;

import com.bugtracker.app.dto.*;
import com.bugtracker.app.entity.User;
import com.bugtracker.app.enums.IssueStatus;
import com.bugtracker.app.enums.Priority;
import com.bugtracker.app.service.IssueService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/issues")
@RequiredArgsConstructor
@Tag(name = "Issues", description = "Issue tracking and status workflow")
public class IssueController {

    private final IssueService issueService;

    @PostMapping
    public ResponseEntity<IssueResponse> createIssue(
            @Valid @RequestBody IssueRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(issueService.createIssue(request, currentUser));
    }

    @GetMapping
    public ResponseEntity<Page<IssueResponse>> listIssues(
            @RequestParam Long projectId,
            @RequestParam(required = false) IssueStatus status,
            @RequestParam(required = false) Priority priority,
            Pageable pageable) {
        return ResponseEntity.ok(issueService.listIssues(projectId, status, priority, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IssueResponse> getIssue(@PathVariable Long id) {
        return ResponseEntity.ok(issueService.getIssue(id));
    }

    // Only ADMIN or DEVELOPER can be assignees / do the assigning —
    // adjust this rule if you want REPORTERs to self-assign, etc.
    @PreAuthorize("hasAnyRole('ADMIN', 'DEVELOPER')")
    @PatchMapping("/{id}/assign")
    public ResponseEntity<IssueResponse> assignIssue(
            @PathVariable Long id,
            @RequestParam Long assigneeId) {
        return ResponseEntity.ok(issueService.assignIssue(id, assigneeId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DEVELOPER')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<IssueResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam IssueStatus status) {
        return ResponseEntity.ok(issueService.updateStatus(id, status));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(issueService.addComment(id, request, currentUser));
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long id) {
        return ResponseEntity.ok(issueService.getComments(id));
    }
}