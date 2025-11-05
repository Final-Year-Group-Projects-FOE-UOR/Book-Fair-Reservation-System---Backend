package com.bookfair.bookfairreservationsystembackend.services.auth;

import com.bookfair.bookfairreservationsystembackend.dtos.request.LoginRequest;
import com.bookfair.bookfairreservationsystembackend.dtos.response.LoginResponse;
import com.bookfair.bookfairreservationsystembackend.models.User;
import com.bookfair.bookfairreservationsystembackend.repositories.UserRepository;
import com.bookfair.bookfairreservationsystembackend.services.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public LoginResponse verifyUser(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        if (authentication.isAuthenticated()) {
            User user = userRepository.findByUsername(request.username());
            String jwt = jwtService.generateToken(user.getUsername(), user.getRole().name());
            return new LoginResponse(jwt, user.getUsername(), user.getRole().name());
        }
        return null;
    }
}
