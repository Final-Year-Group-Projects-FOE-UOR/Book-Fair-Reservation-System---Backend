package com.bookfair.bookfairreservationsystembackend.controllers.admin;
import com.bookfair.bookfairreservationsystembackend.dtos.request.ModeratorRegisterRequest;
import com.bookfair.bookfairreservationsystembackend.models.User;
import com.bookfair.bookfairreservationsystembackend.dtos.response.ApiResponse;
import com.bookfair.bookfairreservationsystembackend.services.admin.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import com.bookfair.bookfairreservationsystembackend.models.Role;
import java.util.List;
@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/admin")
//@RequestMapping("/api/v2/admin")
public class AdminController {

    private final AdminService adminService;


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dashboard")
    public String dashboard() {
        return "Welcome Admin! You can manage servants and vendors.";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-moderator")
    public ResponseEntity<ApiResponse> createModerator(@RequestBody ModeratorRegisterRequest request) {
        try {
            User moderator = adminService.createModerator(request);
            return ResponseEntity.ok(new ApiResponse(true, "Moderator created successfully", moderator));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse(false, "An unexpected error occurred.admin already exist", null));
        }
    }


    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/check-admin")
    public String checkAdmin() {
        return adminService.checkAdminExists()
                ? "Admin already exists."
                : "No admin account found.";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete-moderator/{username}")
    public ResponseEntity<ApiResponse> deleteModerator(@PathVariable String username) {
        boolean deleted = adminService.deleteModerator(username);
        if (!deleted)
            return ResponseEntity.status(404).body(new ApiResponse(false, "Moderator not found", null));
        return ResponseEntity.ok(new ApiResponse(true, "Moderator deleted successfully", null));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<ApiResponse> listAllUsers() {
        try {
            List<User> users = adminService.getUsersByRole(Role.ROLE_USER);
            return ResponseEntity.ok(new ApiResponse(true, "All users fetched successfully", users));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse(false, "Unexpected server error", null));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/moderators")
    public ResponseEntity<ApiResponse> listAllModerators() {
        try {
            List<User> moderators = adminService.getUsersByRole(Role.ROLE_MODERATOR);
            return ResponseEntity.ok(new ApiResponse(true, "All moderators fetched successfully", moderators));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse(false, "Unexpected server error", null));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/users/suspend")
    public ResponseEntity<ApiResponse>suspendUser(@RequestParam String email) {
        try {
            boolean result = adminService.suspendUser(email);
            return ResponseEntity.ok(new ApiResponse(true, "User suspended successfully", result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse(false, "Unexpected server error", null));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/users/activate")
    public ResponseEntity<ApiResponse>activateUser(@RequestParam String email) {
        try {
            boolean result = adminService.activeUser(email);
            return ResponseEntity.ok(new ApiResponse(true, "User activated successfully", result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse(false, "Unexpected server error" ,null));
        }
    }

}
