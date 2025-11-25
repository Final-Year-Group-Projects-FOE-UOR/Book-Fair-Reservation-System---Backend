package com.bookfair.bookfairreservationsystembackend.controllers.user;

import com.bookfair.bookfairreservationsystembackend.dtos.request.EmailRequest;
import com.bookfair.bookfairreservationsystembackend.dtos.request.ResetPasswordRequest;
import com.bookfair.bookfairreservationsystembackend.dtos.request.UserRejisterRequest;
import com.bookfair.bookfairreservationsystembackend.dtos.response.UserValidationResponse;
import com.bookfair.bookfairreservationsystembackend.models.user.User;
import com.bookfair.bookfairreservationsystembackend.dtos.response.ApiResponse;
import com.bookfair.bookfairreservationsystembackend.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
//@RequestMapping("/api/v2/users")
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@RequestBody UserRejisterRequest request) {
        try {
            User newUser = userService.registerUser(request);
            return ResponseEntity.ok(new ApiResponse(true, "Vendor registered successfully", newUser));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    @GetMapping("/auth/{username}")
    public ResponseEntity<ApiResponse> getUserByEmail(@PathVariable String email) {
        User user = userService.findUserByEmail(email); // updated method name
        if (user == null) {
            return ResponseEntity.status(404).body(new ApiResponse(false,"User not found", null));
        }
        return ResponseEntity.ok(new ApiResponse(true,"User fetched successfully", user));
    }

    @PostMapping("/password-request-reset")
    public ResponseEntity<ApiResponse> requestPasswordReset(@RequestBody EmailRequest request) {
        try {
            userService.requestResetPassword(request.email());
            return ResponseEntity.ok(new ApiResponse(true, "Password reset email sent", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PostMapping("/reset-password")

    public ResponseEntity<ApiResponse> resetPassword(
            @RequestParam String token,
            @RequestBody ResetPasswordRequest request) {
        try {
            userService.resetPassword(token, request.newPassword());
            return ResponseEntity.ok(new ApiResponse(true, "Password reset successfully", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    @GetMapping("/validate")
    public ResponseEntity<ApiResponse> validateUser() {
        UserValidationResponse userValidation = userService.validateAuthenticatedUser();
        return ResponseEntity.ok(new ApiResponse(true, "User is valid", userValidation));
    }

}
