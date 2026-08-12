package com.bugtracker.app.service;

import com.bugtracker.app.dto.RoleUpdateRequest;
import com.bugtracker.app.dto.UserResponse;
import com.bugtracker.app.entity.User;
import com.bugtracker.app.exception.ResourceNotFoundException;
import com.bugtracker.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponse> listUsers() {
        return userRepository.findAll().stream()
                .map(u -> new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getRole()))
                .collect(Collectors.toList());
    }

    public UserResponse updateRole(Long userId, RoleUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        user.setRole(request.getRole());
        User saved = userRepository.save(user);
        return new UserResponse(saved.getId(), saved.getName(), saved.getEmail(), saved.getRole());
    }
}