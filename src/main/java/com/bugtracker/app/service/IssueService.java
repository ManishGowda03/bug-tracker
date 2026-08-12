package com.bugtracker.app.service;

import com.bugtracker.app.dto.*;
import com.bugtracker.app.entity.Comment;
import com.bugtracker.app.entity.Issue;
import com.bugtracker.app.entity.Project;
import com.bugtracker.app.entity.User;
import com.bugtracker.app.enums.IssueStatus;
import com.bugtracker.app.enums.Priority;
import com.bugtracker.app.exception.InvalidStatusTransitionException;
import com.bugtracker.app.exception.ResourceNotFoundException;
import com.bugtracker.app.repository.CommentRepository;
import com.bugtracker.app.repository.IssueRepository;
import com.bugtracker.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IssueService {

    private final IssueRepository issueRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ProjectService projectService;

    // ---- The status-workflow rule: this map is the whole "state machine." ----
    // Key = current status, Value = the set of statuses it's allowed to move to.
    // OPEN can only go to IN_PROGRESS. IN_PROGRESS can go to RESOLVED (fixed)
    // or back to OPEN (reopened). RESOLVED can go to CLOSED or back to
    // IN_PROGRESS (rejected fix). CLOSED is terminal — nothing moves out of it.
    private static final Map<IssueStatus, Set<IssueStatus>> ALLOWED_TRANSITIONS = new EnumMap<>(IssueStatus.class);
    static {
        ALLOWED_TRANSITIONS.put(IssueStatus.OPEN, Set.of(IssueStatus.IN_PROGRESS));
        ALLOWED_TRANSITIONS.put(IssueStatus.IN_PROGRESS, Set.of(IssueStatus.RESOLVED, IssueStatus.OPEN));
        ALLOWED_TRANSITIONS.put(IssueStatus.RESOLVED, Set.of(IssueStatus.CLOSED, IssueStatus.IN_PROGRESS));
        ALLOWED_TRANSITIONS.put(IssueStatus.CLOSED, Set.of());
    }

    public IssueResponse createIssue(IssueRequest request, User reporter) {
        Project project = projectService.getProjectEntity(request.getProjectId());

        Issue issue = Issue.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .project(project)
                .reporter(reporter)
                .build();

        Issue saved = issueRepository.save(issue);
        return toResponse(saved);
    }

    public Page<IssueResponse> listIssues(Long projectId, IssueStatus status, Priority priority, Pageable pageable) {
        Page<Issue> issues;
        if (status != null && priority != null) {
            issues = issueRepository.findByProjectIdAndStatusAndPriority(projectId, status, priority, pageable);
        } else {
            issues = issueRepository.findByProjectId(projectId, pageable);
        }
        return issues.map(this::toResponse);
    }

    public IssueResponse getIssue(Long id) {
        return toResponse(getIssueEntity(id));
    }

    public IssueResponse assignIssue(Long issueId, Long assigneeId) {
        Issue issue = getIssueEntity(issueId);
        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + assigneeId));

        issue.setAssignee(assignee);
        issue.setUpdatedAt(LocalDateTime.now());
        return toResponse(issueRepository.save(issue));
    }

    public IssueResponse updateStatus(Long issueId, IssueStatus newStatus) {
        Issue issue = getIssueEntity(issueId);
        IssueStatus currentStatus = issue.getStatus();

        Set<IssueStatus> allowedNext = ALLOWED_TRANSITIONS.get(currentStatus);
        if (allowedNext == null || !allowedNext.contains(newStatus)) {
            throw new InvalidStatusTransitionException(
                    "Cannot move issue from " + currentStatus + " to " + newStatus);
        }

        issue.setStatus(newStatus);
        issue.setUpdatedAt(LocalDateTime.now());
        return toResponse(issueRepository.save(issue));
    }

    public CommentResponse addComment(Long issueId, CommentRequest request, User author) {
        Issue issue = getIssueEntity(issueId);

        Comment comment = Comment.builder()
                .text(request.getText())
                .issue(issue)
                .author(author)
                .build();

        Comment saved = commentRepository.save(comment);
        return new CommentResponse(saved.getId(), saved.getText(), author.getName(), saved.getCreatedAt());
    }

    public List<CommentResponse> getComments(Long issueId) {
        return commentRepository.findByIssueIdOrderByCreatedAtAsc(issueId).stream()
                .map(c -> new CommentResponse(c.getId(), c.getText(), c.getAuthor().getName(), c.getCreatedAt()))
                .collect(Collectors.toList());
    }

    private Issue getIssueEntity(Long id) {
        return issueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found: " + id));
    }

    private IssueResponse toResponse(Issue issue) {
        return new IssueResponse(
                issue.getId(),
                issue.getTitle(),
                issue.getDescription(),
                issue.getStatus(),
                issue.getPriority(),
                issue.getProject().getName(),
                issue.getReporter().getName(),
                issue.getAssignee() != null ? issue.getAssignee().getName() : null,
                issue.getCreatedAt(),
                issue.getUpdatedAt()
        );
    }
}