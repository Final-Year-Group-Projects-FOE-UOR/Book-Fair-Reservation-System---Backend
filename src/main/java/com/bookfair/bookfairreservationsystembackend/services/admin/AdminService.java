package com.bookfair.bookfairreservationsystembackend.services.admin;

import com.bookfair.bookfairreservationsystembackend.dtos.request.ModeratorRegisterRequest;
import com.bookfair.bookfairreservationsystembackend.models.user.Role;
import com.bookfair.bookfairreservationsystembackend.models.user.User;
import com.bookfair.bookfairreservationsystembackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

// services/admin/AdminService.java
@Service
@RequiredArgsConstructor

public class AdminService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public User createModerator(ModeratorRegisterRequest request) {

        if (userRepository.findByEmail(request.email()) != null) {
            throw new IllegalArgumentException("Email already registered. Please use another email.");
        }
        User moderator = new User();
        moderator.setEmail(request.email());
        moderator.setUsername(request.username());
        moderator.setPassword(passwordEncoder.encode(request.password()));
        moderator.setRole(Role.ROLE_MODERATOR);
        return userRepository.save(moderator);
    }

    public boolean deleteModerator(String email) {
        User moderator = userRepository.findByEmail(email);
        if (moderator != null && moderator.getRole() == Role.ROLE_MODERATOR) {
            userRepository.delete(moderator);
            return true;
        }
        return false;
    }

    public boolean checkAdminExists() {
        return userRepository.findByEmail("admin@gmail.com") != null;
    }

    public List<User>getUsersByRole(Role role){
        List<User> users = userRepository.findByRole(role);
        if(users.isEmpty()){
            throw  new IllegalArgumentException("Users not found with role: " + role);
        }
        return users;
    }

    public  boolean suspendUser(String email){
        User user = userRepository.findByEmail(email);
        if(user == null){
            throw  new IllegalArgumentException("User not found with email: " + email);
        }
        if(user.getRole() == Role.ROLE_MODERATOR){
            throw  new IllegalArgumentException("Cannot suspend a moderator: " + email);
        }
        if(user.getRole() == Role.ROLE_ADMIN){
            throw  new IllegalArgumentException("Cannot suspend an admin: " + email);
        }
        user.setActive(false);
        userRepository.save(user);
        return true;
    }

    public  boolean activeUser(String email){
        User user = userRepository.findByEmail(email);
        if(user == null){
            throw  new IllegalArgumentException("User not found with email: " + email);
        }
        if(user.getRole() == Role.ROLE_MODERATOR){
            throw  new IllegalArgumentException("Cannot activate a moderator: " + email);
        }
        if(user.getRole() == Role.ROLE_ADMIN){
            throw  new IllegalArgumentException("Cannot activate an admin: " + email);
        }
        user.setActive(true);
        userRepository.save(user);
        return true;
    }
}
