package com.bookfair.bookfairreservationsystembackend.controllers;

import com.bookfair.bookfairreservationsystembackend.dtos.UserDto;
import com.bookfair.bookfairreservationsystembackend.dtos.LoginDto;
import com.bookfair.bookfairreservationsystembackend.models.User;
import com.bookfair.bookfairreservationsystembackend.responses.ApiResponse;
import com.bookfair.bookfairreservationsystembackend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(
            @RequestBody User user
    ){
        User newUser =  userService.registerUser(user);
        return ResponseEntity.ok(new ApiResponse(true,"User registered successfully", newUser));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(
            @RequestBody UserDto user
    ){
        LoginDto loginDto = userService.verifyUser(user);
        if(loginDto == null){
            return ResponseEntity.status(401)
                    .body(new ApiResponse(false,"Invalid username or password", null));
        }
        return ResponseEntity.ok(new ApiResponse(true,"User logged in successfully", loginDto));
    }

    @GetMapping("/auth/{username}")
    public ResponseEntity<ApiResponse> getUserByUsername(
            @PathVariable String username
    ){
        String user = userService.findByUsername(username);
        if(user == null){
            return ResponseEntity.status(404).body(new ApiResponse(false,"User not found", null));
        }
        return ResponseEntity.ok(new ApiResponse(true,"User fetched successfully", user));
    }


}
