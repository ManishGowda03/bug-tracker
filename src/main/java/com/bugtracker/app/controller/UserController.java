package com.bugtracker.app.controller;

import com.bugtracker.app.dto.RoleUpdateRequest;
import com.bugtracker.app.dto.UserResponse;
import com.bugtracker.app.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User listing and role management (admin only)")
public class UserController {

    private final UserService userService;

    // Only an existing ADMIN can view all users and promote/demote roles.
    // This is what closes the privilege-escalation gap — role changes are
    // no longer something a user can grant themselves at registration.
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserResponse>> listUsers() {
        return ResponseEntity.ok(userService.listUsers());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/role")
    public ResponseEntity<UserResponse> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleUpdateRequest request) {
        return ResponseEntity.ok(userService.updateRole(id, request));
    }
}