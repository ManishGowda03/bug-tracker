package com.bugtracker.app.dto;

import com.bugtracker.app.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleUpdateRequest {

    @NotNull(message = "Role is required")
    private Role role;
}