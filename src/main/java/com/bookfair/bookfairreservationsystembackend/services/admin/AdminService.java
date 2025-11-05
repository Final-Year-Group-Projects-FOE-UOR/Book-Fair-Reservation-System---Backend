package com.bookfair.bookfairreservationsystembackend.services.admin;

import com.bookfair.bookfairreservationsystembackend.dtos.request.ModeratorRegisterRequest;
import com.bookfair.bookfairreservationsystembackend.models.Role;
import com.bookfair.bookfairreservationsystembackend.models.User;
import com.bookfair.bookfairreservationsystembackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

// services/admin/AdminService.java
@Service
@RequiredArgsConstructor

public class AdminService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public User createModerator(ModeratorRegisterRequest request) {
        if (userRepository.findByUsername(request.username()) != null) {
            throw new IllegalArgumentException("Username already exists. Please choose another username.");
        }
        User moderator = new User();
        moderator.setUsername(request.username());
        moderator.setPassword(passwordEncoder.encode(request.password()));
        moderator.setRole(Role.ROLE_MODERATOR);
        return userRepository.save(moderator);
    }

    public boolean deleteModerator(String username) {
        User moderator = userRepository.findByUsername(username);
        if (moderator != null && moderator.getRole() == Role.ROLE_MODERATOR) {
            userRepository.delete(moderator);
            return true;
        }
        return false;
    }

    public boolean checkAdminExists() {
        return userRepository.findByUsername("admin") != null;
    }
}
