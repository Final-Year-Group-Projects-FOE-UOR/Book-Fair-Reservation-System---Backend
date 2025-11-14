package com.bookfair.bookfairreservationsystembackend.services.auth;
import com.bookfair.bookfairreservationsystembackend.dtos.request.LoginRequest;
import com.bookfair.bookfairreservationsystembackend.dtos.response.LoginResponse;
import com.bookfair.bookfairreservationsystembackend.models.user.Role;
import com.bookfair.bookfairreservationsystembackend.models.user.User;
import com.bookfair.bookfairreservationsystembackend.repositories.UserRepository;
import com.bookfair.bookfairreservationsystembackend.services.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class GoogleAuthService {
    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;

    public LoginResponse hangleGoogleLogin(OAuth2User oAuth2User) {
        Map<String, Object> details = oAuth2User.getAttributes();
        String email = (String) details.get("email");
        String name = (String) details.get("name");

        if (email == null || name == null) {
            throw new IllegalArgumentException("Email or Name not found in Google user details");
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setUsername(name);
            user.setPassword(passwordEncoder.encode("GOOGLE_AUTH"));
            user.setRole(Role.ROLE_USER);
            userRepository.save(user);
        }

        String jwt = jwtService.generateToken(user.getUsername(), user.getEmail(), user.getRole().name());
        return new LoginResponse(jwt, user.getEmail(), user.getRole().name());

    }

}
