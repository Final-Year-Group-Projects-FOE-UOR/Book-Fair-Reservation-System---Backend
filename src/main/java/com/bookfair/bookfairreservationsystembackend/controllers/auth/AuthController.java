package com.bookfair.bookfairreservationsystembackend.controllers.auth;


import com.bookfair.bookfairreservationsystembackend.dtos.request.LoginRequest;
import com.bookfair.bookfairreservationsystembackend.dtos.response.ApiResponse;
import com.bookfair.bookfairreservationsystembackend.dtos.response.LoginResponse;
import com.bookfair.bookfairreservationsystembackend.services.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping("/api/v2/users")
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request){
        LoginResponse response = authService.verifyUser(request);
        if(response == null){
            return ResponseEntity.status(401)
                    .body(new ApiResponse(false, "Invalid username or password",null));
        }
        return ResponseEntity.ok(new ApiResponse(true, "Login successful", response));
    }
}
