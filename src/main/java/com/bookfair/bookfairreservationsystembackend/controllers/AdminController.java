package com.bookfair.bookfairreservationsystembackend.controllers;

import com.bookfair.bookfairreservationsystembackend.dtos.request.ModeratorRegisterRequest;
import com.bookfair.bookfairreservationsystembackend.dtos.response.UserResponse;
import com.bookfair.bookfairreservationsystembackend.models.User;
import com.bookfair.bookfairreservationsystembackend.responses.ApiResponse;
import com.bookfair.bookfairreservationsystembackend.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v2/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dashboard")
    public String dashboard() {
        return "Welcome Admin! You can manage servants and vendors.";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-moderator")
    public ResponseEntity<ApiResponse> createModerator(@RequestBody ModeratorRegisterRequest request) {
        if (userService.findUserByUsername(request.username()) != null) {
            return ResponseEntity.status(400)
                    .body(new ApiResponse(false, "Username already exists", null));

        }
        User moderator = userService.createModerator(request);
        UserResponse response = new UserResponse(moderator.getId(), moderator.getUsername(), moderator.getRole().name());
        return ResponseEntity.ok(new ApiResponse(true, "Moderator created successfully", response));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/check-admin")
    public String checkAdmin() {
        User admin = userService.findUserByUsername("admin"); // updated method name
        if (admin == null) {
            return "No admin account found.";
        } else {
            return "Admin already exists! Username: " + admin.getUsername();
        }
    }

    @DeleteMapping("/delete-moderator/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteModerator(@PathVariable String username) {
        boolean deleted = userService.deleteModerator(username);
        if (!deleted) {
            return ResponseEntity.status(404).body(new ApiResponse(false, "Moderator not found", null));
        }
        return ResponseEntity.ok(new ApiResponse(true, "Moderator deleted successfully", null));
    }

}
