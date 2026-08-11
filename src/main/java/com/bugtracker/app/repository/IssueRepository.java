package com.bugtracker.app.repository;

import com.bugtracker.app.entity.Issue;
import com.bugtracker.app.enums.IssueStatus;
import com.bugtracker.app.enums.Priority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueRepository extends JpaRepository<Issue, Long> {

    // Spring Data JPA generates the query just from this method name —
    // no SQL written by hand. Each "And" maps to a WHERE clause.
    Page<Issue> findByProjectIdAndStatusAndPriority(
            Long projectId, IssueStatus status, Priority priority, Pageable pageable);

    Page<Issue> findByProjectId(Long projectId, Pageable pageable);

    Page<Issue> findByAssigneeId(Long assigneeId, Pageable pageable);
}