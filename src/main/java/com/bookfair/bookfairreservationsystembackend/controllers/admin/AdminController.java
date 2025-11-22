package com.bookfair.bookfairreservationsystembackend.controllers.admin;
import com.bookfair.bookfairreservationsystembackend.dtos.request.ModeratorRegisterRequest;
import com.bookfair.bookfairreservationsystembackend.models.user.User;
import com.bookfair.bookfairreservationsystembackend.dtos.response.ApiResponse;
import com.bookfair.bookfairreservationsystembackend.services.admin.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import com.bookfair.bookfairreservationsystembackend.models.user.Role;
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
        User moderator = adminService.createModerator(request);
        return ResponseEntity.ok(new ApiResponse(true, "Moderator created successfully", moderator));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete-moderator/{email}")
    public ResponseEntity<ApiResponse> deleteModerator(@PathVariable String email) {
        adminService.deleteModerator(email);
        return ResponseEntity.ok(new ApiResponse(true, "Moderator deleted successfully", null));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<ApiResponse> listAllUsers() {
        return ResponseEntity.ok(new ApiResponse(true, "All users fetched successfully",
                adminService.getUsersByRole(Role.ROLE_USER)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/moderators")
    public ResponseEntity<ApiResponse> listAllModerators() {
        return ResponseEntity.ok(new ApiResponse(true, "All moderators fetched successfully",
                adminService.getUsersByRole(Role.ROLE_MODERATOR)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/users/suspend")
    public ResponseEntity<ApiResponse> suspendUser(@RequestParam String email) {
        adminService.suspendUser(email);
        return ResponseEntity.ok(new ApiResponse(true, "User suspended successfully", null));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/users/activate")
    public ResponseEntity<ApiResponse> activateUser(@RequestParam String email) {
        adminService.activeUser(email);
        return ResponseEntity.ok(new ApiResponse(true, "User activated successfully", null));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/check-admin")
    public String checkAdmin() {
        return adminService.checkAdminExists() ? "Admin already exists." : "No admin account found.";
    }

}





