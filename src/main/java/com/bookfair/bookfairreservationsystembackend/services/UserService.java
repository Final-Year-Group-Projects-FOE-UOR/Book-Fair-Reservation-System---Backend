package com.bookfair.bookfairreservationsystembackend.services;

import com.bookfair.bookfairreservationsystembackend.dtos.request.LoginRequest;
import com.bookfair.bookfairreservationsystembackend.dtos.request.ModeratorRegisterRequest;
import com.bookfair.bookfairreservationsystembackend.dtos.request.UserRejisterRequest;
import com.bookfair.bookfairreservationsystembackend.dtos.response.LoginResponse;
import com.bookfair.bookfairreservationsystembackend.models.User;
import com.bookfair.bookfairreservationsystembackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.bookfair.bookfairreservationsystembackend.models.Role;
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public User registerUser(UserRejisterRequest request) {
        User user = new User();
        user.setUsername(request.username());
        user.setPassword(bCryptPasswordEncoder.encode(request.password()));

        if(user.getRole()==null){
            user.setRole(Role.ROLE_USER);
        }
        return userRepository.save(user);
    }

    public LoginResponse verifyUser(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        if (authentication.isAuthenticated()) {
            User user = userRepository.findByUsername(request.username());
            String jwt = jwtService.generateToken(user.getUsername(),user.getRole().name());
            return new LoginResponse(jwt, user.getUsername(), user.getRole().name());
        } else {
            return null;
        }
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User createModerator(ModeratorRegisterRequest request) {
        User user = new User();
        user.setUsername(request.username());
        user.setPassword(bCryptPasswordEncoder.encode(request.password()));
        user.setRole(Role.ROLE_MODERATOR);
        return userRepository.save(user);
    }

    public boolean deleteModerator(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getRole() == Role.ROLE_MODERATOR) {
            userRepository.delete(user);
            return true;
        }
        return false;
    }
}