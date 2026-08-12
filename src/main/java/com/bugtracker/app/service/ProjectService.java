package com.bugtracker.app.service;

import com.bugtracker.app.dto.ProjectRequest;
import com.bugtracker.app.dto.ProjectResponse;
import com.bugtracker.app.entity.Project;
import com.bugtracker.app.entity.User;
import com.bugtracker.app.exception.ResourceNotFoundException;
import com.bugtracker.app.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectResponse createProject(ProjectRequest request, User creator) {
        if (projectRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("A project with this name already exists");
        }

        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .createdBy(creator)
                .build();

        Project saved = projectRepository.save(project);
        return toResponse(saved);
    }

    public Page<ProjectResponse> listProjects(Pageable pageable) {
        return projectRepository.findAll(pageable).map(this::toResponse);
    }

    public ProjectResponse getProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + id));
        return toResponse(project);
    }

    // Package-private so IssueService can reuse it to fetch the entity
    // (not just the DTO) when linking an issue to a project.
    Project getProjectEntity(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + id));
    }

    private ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getCreatedBy().getName(),
                project.getCreatedAt()
        );
    }
}