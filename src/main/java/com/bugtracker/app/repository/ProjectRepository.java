package com.bugtracker.app.repository;

import com.bugtracker.app.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    boolean existsByName(String name);
}