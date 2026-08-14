package com.bugtracker.app.service;

import com.bugtracker.app.entity.Issue;
import com.bugtracker.app.entity.Project;
import com.bugtracker.app.enums.IssueStatus;
import com.bugtracker.app.exception.InvalidStatusTransitionException;
import com.bugtracker.app.exception.ResourceNotFoundException;
import com.bugtracker.app.repository.CommentRepository;
import com.bugtracker.app.repository.IssueRepository;
import com.bugtracker.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import com.bugtracker.app.entity.Project;
import com.bugtracker.app.entity.User;

/**
 * Tests focus on the status-transition state machine in IssueService,
 * since that's the core business rule of this project — not just CRUD.
 */
@ExtendWith(MockitoExtension.class)
class IssueServiceTest {

    @Mock
    private IssueRepository issueRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private IssueService issueService;

    private Issue issue;

    @BeforeEach
    void setUp() {
        Project project = new Project();
        project.setId(1L);
        project.setName("Sample Project");

        User reporter = new User();
        reporter.setId(1L);
        reporter.setName("Test Reporter");

        issue = new Issue();
        issue.setId(1L);
        issue.setStatus(IssueStatus.OPEN);
        issue.setProject(project);
        issue.setReporter(reporter);
    }

    // ---- Valid transitions ----

    @Test
    void openToInProgress_isAllowed() {
        when(issueRepository.findById(1L)).thenReturn(Optional.of(issue));
        when(issueRepository.save(any(Issue.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = issueService.updateStatus(1L, IssueStatus.IN_PROGRESS);

        assertEquals(IssueStatus.IN_PROGRESS, result.getStatus());
    }

    @Test
    void inProgressToResolved_isAllowed() {
        issue.setStatus(IssueStatus.IN_PROGRESS);
        when(issueRepository.findById(1L)).thenReturn(Optional.of(issue));
        when(issueRepository.save(any(Issue.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = issueService.updateStatus(1L, IssueStatus.RESOLVED);

        assertEquals(IssueStatus.RESOLVED, result.getStatus());
    }

    @Test
    void resolvedToClosed_isAllowed() {
        issue.setStatus(IssueStatus.RESOLVED);
        when(issueRepository.findById(1L)).thenReturn(Optional.of(issue));
        when(issueRepository.save(any(Issue.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = issueService.updateStatus(1L, IssueStatus.CLOSED);

        assertEquals(IssueStatus.CLOSED, result.getStatus());
    }

    @Test
    void resolvedBackToInProgress_isAllowed() {
        // A rejected fix should be reopenable for more work, not stuck.
        issue.setStatus(IssueStatus.RESOLVED);
        when(issueRepository.findById(1L)).thenReturn(Optional.of(issue));
        when(issueRepository.save(any(Issue.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = issueService.updateStatus(1L, IssueStatus.IN_PROGRESS);

        assertEquals(IssueStatus.IN_PROGRESS, result.getStatus());
    }

    // ---- Invalid transitions ----

    @Test
    void openToClosed_isRejected() {
        // Can't skip straight to closed — must pass through the workflow.
        when(issueRepository.findById(1L)).thenReturn(Optional.of(issue));

        assertThrows(InvalidStatusTransitionException.class,
                () -> issueService.updateStatus(1L, IssueStatus.CLOSED));

        verify(issueRepository, never()).save(any());
    }

    @Test
    void closedIssue_cannotTransitionToAnything() {
        // CLOSED is terminal — nothing moves out of it.
        issue.setStatus(IssueStatus.CLOSED);
        when(issueRepository.findById(1L)).thenReturn(Optional.of(issue));

        assertThrows(InvalidStatusTransitionException.class,
                () -> issueService.updateStatus(1L, IssueStatus.OPEN));

        verify(issueRepository, never()).save(any());
    }

    @Test
    void updateStatus_issueNotFound_throwsResourceNotFound() {
        when(issueRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> issueService.updateStatus(99L, IssueStatus.IN_PROGRESS));
    }
}