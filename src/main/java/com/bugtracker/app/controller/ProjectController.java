package com.bugtracker.app.controller;

import com.bugtracker.app.dto.ProjectRequest;
import com.bugtracker.app.dto.ProjectResponse;
import com.bugtracker.app.entity.User;
import com.bugtracker.app.service.ProjectService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Project management")
public class ProjectController {

    private final ProjectService projectService;

    // Only ADMIN can create projects — @PreAuthorize checks the role
    // baked into the JWT-derived authentication before the method runs.
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @RequestBody ProjectRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(projectService.createProject(request, currentUser));
    }

    @GetMapping
    public ResponseEntity<Page<ProjectResponse>> listProjects(Pageable pageable) {
        return ResponseEntity.ok(projectService.listProjects(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProject(id));
    }
}