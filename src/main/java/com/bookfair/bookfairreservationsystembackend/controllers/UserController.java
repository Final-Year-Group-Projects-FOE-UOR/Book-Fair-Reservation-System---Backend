package com.bookfair.bookfairreservationsystembackend.controllers;

import com.bookfair.bookfairreservationsystembackend.dtos.request.LoginRequest;
import com.bookfair.bookfairreservationsystembackend.dtos.request.ModeratorRegisterRequest;
import com.bookfair.bookfairreservationsystembackend.dtos.request.UserRejisterRequest;
import com.bookfair.bookfairreservationsystembackend.dtos.response.LoginResponse;
import com.bookfair.bookfairreservationsystembackend.dtos.response.UserResponse;
import com.bookfair.bookfairreservationsystembackend.models.User;
import com.bookfair.bookfairreservationsystembackend.responses.ApiResponse;
import com.bookfair.bookfairreservationsystembackend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v2/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@RequestBody UserRejisterRequest request) {
        //user.setRole(Role.ROLE_USER);
        User newUser = userService.registerUser(request);
        UserResponse response = new UserResponse(newUser.getId(), newUser.getUsername(), newUser.getRole().name());
        return ResponseEntity.ok(new ApiResponse(true, "Vendor registered successfully", newUser));
    }

    @PostMapping("/register-moderator")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> registerServant(@RequestBody ModeratorRegisterRequest request) {
        User moderator = userService.createModerator(request);
        UserResponse response = new UserResponse(moderator.getId(), moderator.getUsername(), moderator.getRole().name());
        return ResponseEntity.ok(new ApiResponse(true, "Servant created successfully by Admin", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request){
        LoginResponse response = userService.verifyUser(request);
        if(response == null){
            return ResponseEntity.status(401)
                    .body(new ApiResponse(false,"Invalid username or password", null));
        }
        return ResponseEntity.ok(new ApiResponse(true,"User logged in successfully", response));
    }

    @GetMapping("/auth/{username}")
    public ResponseEntity<ApiResponse> getUserByUsername(@PathVariable String username) {
        User user = userService.findUserByUsername(username); // updated method name
        if (user == null) {
            return ResponseEntity.status(404).body(new ApiResponse(false,"User not found", null));
        }
        return ResponseEntity.ok(new ApiResponse(true,"User fetched successfully", user));
    }



}
