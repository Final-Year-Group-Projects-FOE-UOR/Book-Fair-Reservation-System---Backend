package com.bookfair.bookfairreservationsystembackend.services.auth;

import com.bookfair.bookfairreservationsystembackend.dtos.request.LoginRequest;
import com.bookfair.bookfairreservationsystembackend.dtos.response.LoginResponse;
import com.bookfair.bookfairreservationsystembackend.exception.NotFoundException;
import com.bookfair.bookfairreservationsystembackend.models.user.Role;
import com.bookfair.bookfairreservationsystembackend.models.user.User;
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
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        if (authentication.isAuthenticated()) {
            User user = userRepository.findByEmail(request.email())
                    .orElseThrow(() -> new NotFoundException("User not found"));

            // First-time login check for moderators
            if (user.getRole() == Role.ROLE_MODERATOR && user.isFirstTimeLogin()) {
                // Return a special flag to indicate password change is required
                return new LoginResponse(null, user.getEmail(), user.getRole().name() + "_PASSWORD_CHANGE_REQUIRED");
            }

            String jwt = jwtService.generateToken(user.getUsername(), user.getEmail(), user.getRole().name());
            return new LoginResponse(jwt, user.getEmail(), user.getRole().name());
        }
        return null;
    }



}
