package com.bookfair.bookfairreservationsystembackend.services;

import com.bookfair.bookfairreservationsystembackend.dtos.UserDto;
import com.bookfair.bookfairreservationsystembackend.dtos.LoginDto;
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

    public User registerUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        if(user.getRole()==null){
            user.setRole(Role.ROLE_USER);
        }
        return userRepository.save(user);
    }

    public LoginDto verifyUser(UserDto userDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDto.username(), userDto.password())
        );

        if (authentication.isAuthenticated()) {
            User user = userRepository.findByUsername(userDto.username());
            String jwt = jwtService.generateToken(user.getUsername(),user.getRole().name());
            return new LoginDto(userDto.username(), jwt);
        } else {
            return null;
        }
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User createModerator(User moderator) {
        moderator.setPassword(bCryptPasswordEncoder.encode(moderator.getPassword()));
        moderator.setRole(Role.ROLE_MODERATOR);
        return userRepository.save(moderator);
    }
}