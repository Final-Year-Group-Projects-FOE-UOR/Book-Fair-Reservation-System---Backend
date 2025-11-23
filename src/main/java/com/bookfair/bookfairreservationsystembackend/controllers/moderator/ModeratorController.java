package com.bookfair.bookfairreservationsystembackend.controllers.moderator;

import com.bookfair.bookfairreservationsystembackend.dtos.request.FirstTimePasswordChangeRequest;
import com.bookfair.bookfairreservationsystembackend.dtos.response.ApiResponse;
import com.bookfair.bookfairreservationsystembackend.models.user.User;
import com.bookfair.bookfairreservationsystembackend.services.moderator.ModeratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/moderator")
@RequiredArgsConstructor
public class ModeratorController {

    private final ModeratorService moderatorService;

    // First-time login password change
    @PostMapping("/change-password-first-time")
    public ResponseEntity<ApiResponse> changePasswordFirstTime(
            @RequestBody FirstTimePasswordChangeRequest request) {

        User updated = moderatorService.changePasswordFirstTime(
                request.email(),
                request.newPassword()
        );

        return ResponseEntity.ok(new ApiResponse(true,
                "Password changed successfully for " + updated.getUsername(), updated));
    }

    // Moderators cannot update password manually
    @PostMapping("/update-password")
    public ResponseEntity<ApiResponse> updatePassword(
            @RequestParam String email,
            @RequestParam String newPassword) {

        moderatorService.updatePasswordBlocked(); // Throws BadRequestException
        return ResponseEntity.badRequest().body(new ApiResponse(false, "Moderators cannot change password", null));
    }
    // Admin resets password
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestParam String email) {
        User updated = moderatorService.resetPasswordToDefault(email);
        return ResponseEntity.ok(new ApiResponse(true,
                "Moderator password reset to default successfully", updated));
    }
}
