package com.bookfair.bookfairreservationsystembackend.controllers;

import com.bookfair.bookfairreservationsystembackend.models.User;
import com.bookfair.bookfairreservationsystembackend.responses.ApiResponse;
import com.bookfair.bookfairreservationsystembackend.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

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
    public ResponseEntity<ApiResponse> createModerator(@RequestBody User userRequest) {
        if(userService.findUserByUsername(userRequest.getUsername()) != null){
            return ResponseEntity.status(400)
                    .body(new ApiResponse(false,"Username already exists", null));

        }
        User moderator = userService.createModerator(userRequest);
        return ResponseEntity.ok(new ApiResponse(true,"Moderator created successfully", moderator));
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
}
