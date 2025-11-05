package com.bookfair.bookfairreservationsystembackend.controllers.user;

import com.bookfair.bookfairreservationsystembackend.dtos.request.LoginRequest;
import com.bookfair.bookfairreservationsystembackend.dtos.request.UserRejisterRequest;
import com.bookfair.bookfairreservationsystembackend.dtos.response.LoginResponse;
import com.bookfair.bookfairreservationsystembackend.dtos.response.UserResponse;
import com.bookfair.bookfairreservationsystembackend.models.User;
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
        //user.setRole(Role.ROLE_USER);
        User newUser = userService.registerUser(request);
        UserResponse response = new UserResponse(newUser.getId(), newUser.getUsername(), newUser.getRole().name());
        return ResponseEntity.ok(new ApiResponse(true, "Vendor registered successfully", newUser));
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
