package com.bookfair.bookfairreservationsystembackend.services.admin;
import com.bookfair.bookfairreservationsystembackend.dtos.request.ModeratorRegisterRequest;
import com.bookfair.bookfairreservationsystembackend.dtos.response.UserResponse;
import com.bookfair.bookfairreservationsystembackend.exception.BadRequestException;
import com.bookfair.bookfairreservationsystembackend.exception.NotFoundException;
import com.bookfair.bookfairreservationsystembackend.models.user.Role;
import com.bookfair.bookfairreservationsystembackend.models.user.User;
import com.bookfair.bookfairreservationsystembackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    private static final String DEFAULT_MODERATOR_PASSWORD = "moderator123";

    // Helper method to map User to UserResponse DTO
    private UserResponse mapToDto(User user) {
        return new UserResponse(user.getId(), user.getEmail(),user.getUsername(),user.getRole().name());
    }

    public UserResponse createModerator(ModeratorRegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new BadRequestException("Email already registered. Please use another email.");
        }

        User moderator = new User();
        moderator.setEmail(request.email());
        moderator.setUsername(request.username());
        moderator.setPassword(passwordEncoder.encode(DEFAULT_MODERATOR_PASSWORD));
        moderator.setRole(Role.ROLE_MODERATOR);
        moderator.setFirstTimeLogin(true);

        User saved = userRepository.save(moderator);
        return mapToDto(saved);
    }

    public UserResponse resetModeratorPassword(String email) {
        User moderator = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Moderator not found"));

        if (moderator.getRole() != Role.ROLE_MODERATOR) {
            throw new BadRequestException("User is not a moderator");
        }

        moderator.setPassword(passwordEncoder.encode(DEFAULT_MODERATOR_PASSWORD));
        moderator.setFirstTimeLogin(true);
        return mapToDto(userRepository.save(moderator));
    }

    public boolean deleteModerator(String email) {
        User moderator = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Moderator not found with email: " + email));

        if (moderator.getRole() != Role.ROLE_MODERATOR) {
            throw new BadRequestException("User with email " + email + " is not a moderator");
        }

        userRepository.delete(moderator);
        return true;
    }

    public boolean checkAdminExists() {
        return userRepository.findByEmail("admin@gmail.com").isPresent();
    }

    public List<UserResponse> getUsersByRole(Role role) {
        List<User> users = userRepository.findByRole(role);
        if (users.isEmpty()) {
            throw new NotFoundException("Users not found with role: " + role);
        }
        return users.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public boolean suspendUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

        if (user.getRole() == Role.ROLE_MODERATOR || user.getRole() == Role.ROLE_ADMIN) {
            throw new BadRequestException("Cannot suspend user with role: " + user.getRole());
        }

        user.setActive(false);
        userRepository.save(user);
        return true;
    }

    public boolean activeUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

        if (user.getRole() == Role.ROLE_MODERATOR || user.getRole() == Role.ROLE_ADMIN) {
            throw new BadRequestException("Cannot activate user with role: " + user.getRole());
        }

        user.setActive(true);
        userRepository.save(user);
        return true;
    }
}


//package com.bookfair.bookfairreservationsystembackend.services.admin;
//
//import com.bookfair.bookfairreservationsystembackend.dtos.request.ModeratorRegisterRequest;
//import com.bookfair.bookfairreservationsystembackend.models.user.Role;
//import com.bookfair.bookfairreservationsystembackend.models.user.User;
//import com.bookfair.bookfairreservationsystembackend.repositories.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Service;
//import java.util.List;
//import com.bookfair.bookfairreservationsystembackend.exception.BadRequestException;
//import com.bookfair.bookfairreservationsystembackend.exception.NotFoundException;
//// services/admin/AdminService.java
//@Service
//@RequiredArgsConstructor
//
//public class AdminService {
//
////    private final UserRepository userRepository;
////    private final BCryptPasswordEncoder passwordEncoder;
////
////    public User createModerator(ModeratorRegisterRequest request) {
////
////        if (userRepository.findByEmail(request.email()) != null) {
////            throw new BadRequestException("Email already registered. Please use another email.");
////        }
////        User moderator = new User();
////        moderator.setEmail(request.email());
////        moderator.setUsername(request.username());
////        moderator.setPassword(passwordEncoder.encode(request.password()));
////        moderator.setRole(Role.ROLE_MODERATOR);
////        return userRepository.save(moderator);
////    }
//
//    private final UserRepository userRepository;
//    private final BCryptPasswordEncoder passwordEncoder;
//
//    private static final String DEFAULT_MODERATOR_PASSWORD = "moderator123"; // configurable
//
//    public User createModerator(ModeratorRegisterRequest request) {
//
//        if (userRepository.findByEmail(request.email()).isPresent()) {
//            throw new BadRequestException("Email already registered. Please use another email.");
//        }
//
//        User moderator = new User();
//        moderator.setEmail(request.email());
//        moderator.setUsername(request.username());
//        moderator.setPassword(passwordEncoder.encode(DEFAULT_MODERATOR_PASSWORD));
//        moderator.setRole(Role.ROLE_MODERATOR);
//        moderator.setFirstTimeLogin(true);
//        return userRepository.save(moderator);
//    }
//
//    // Admin-triggered reset
//    public User resetModeratorPassword(String email) {
//        User moderator = userRepository.findByEmail(email)
//                .orElseThrow(() -> new NotFoundException("Moderator not found"));
//
//        if (moderator.getRole() != Role.ROLE_MODERATOR) {
//            throw new BadRequestException("User is not a moderator");
//        }
//
//        moderator.setPassword(passwordEncoder.encode(DEFAULT_MODERATOR_PASSWORD));
//        moderator.setFirstTimeLogin(true);
//        return userRepository.save(moderator);
//    }
//
//    public boolean deleteModerator(String email) {
//        User moderator = userRepository.findByEmail(email)
//                .orElseThrow(() -> new NotFoundException("Moderator not found with email: " + email));
//
//        if (moderator.getRole() != Role.ROLE_MODERATOR) {
//            throw new BadRequestException("User with email " + email + " is not a moderator");
//        }
//
//        userRepository.delete(moderator);
//        return true;
//    }
//
//
//    public boolean checkAdminExists() {
//        return userRepository.findByEmail("admin@gmail.com") != null;
//    }
//
//    public List<User>getUsersByRole(Role role){
//        List<User> users = userRepository.findByRole(role);
//        if(users.isEmpty()){
//            throw  new NotFoundException("Users not found with role: " + role);
//        }
//        return users;
//    }
//
//    public  boolean suspendUser(String email){
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
////        User user = userRepository.findByEmail(email);
////        if(user == null){
////            throw  new NotFoundException("User not found with email: " + email);
////        }
//        if(user.getRole() == Role.ROLE_MODERATOR){
//            throw  new BadRequestException("Cannot suspend a moderator: " + email);
//        }
//        if(user.getRole() == Role.ROLE_ADMIN){
//            throw  new IllegalArgumentException("Cannot suspend an admin: " + email);
//        }
//        user.setActive(false);
//        userRepository.save(user);
//        return true;
//    }
//
//    public  boolean activeUser(String email){
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
////        User user = userRepository.findByEmail(email);
////        if(user == null){
////            throw  new NotFoundException("User not found with email: " + email);
////        }
//        if(user.getRole() == Role.ROLE_MODERATOR){
//            throw  new BadRequestException("Cannot activate a moderator: " + email);
//        }
//        if(user.getRole() == Role.ROLE_ADMIN){
//            throw  new IllegalArgumentException("Cannot activate an admin: " + email);
//        }
//        user.setActive(true);
//        userRepository.save(user);
//        return true;
//    }
//}
