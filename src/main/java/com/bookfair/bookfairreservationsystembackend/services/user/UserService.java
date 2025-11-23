package com.bookfair.bookfairreservationsystembackend.services.user;

import com.bookfair.bookfairreservationsystembackend.dtos.request.UserRejisterRequest;
import com.bookfair.bookfairreservationsystembackend.exception.BadRequestException;
import com.bookfair.bookfairreservationsystembackend.exception.NotFoundException;
import com.bookfair.bookfairreservationsystembackend.models.user.User;
import com.bookfair.bookfairreservationsystembackend.models.vendor.Vendor;
import com.bookfair.bookfairreservationsystembackend.repositories.UserRepository;
import com.bookfair.bookfairreservationsystembackend.repositories.VendorsRepository;
import com.bookfair.bookfairreservationsystembackend.services.EmailService;
import com.bookfair.bookfairreservationsystembackend.services.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bookfair.bookfairreservationsystembackend.models.user.Role;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final VendorsRepository vendorsRepository;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailService emailService;

    @Transactional
    public User registerUser(UserRejisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email already in use");
        }
        User user = new User();
        user.setEmail(request.email());
        user.setUsername(request.username());
        user.setPassword(bCryptPasswordEncoder.encode(request.password()));

        if (user.getRole() == null) {
            user.setRole(Role.ROLE_USER);
        }
        User savedUser = userRepository.save(user);

        if (savedUser.getRole() == Role.ROLE_USER) {
            Vendor vendor = new Vendor();
            vendor.setUser(savedUser);
            vendor.setUserEmail(savedUser.getEmail());
            vendor.setBusinessName(savedUser.getUsername());
            vendor.setGenres(new java.util.ArrayList<>());
            vendorsRepository.save(vendor);
        }

        return savedUser;
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
    }

    public void requestResetPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User with email " + email + " not found"));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        userRepository.save(user);

        String resetLink = "http://localhost:8000/api/v3/users/reset-password?token=" + token;
        emailService.sendEmail(user.getEmail(), "Reset Password", "Click this ti reset your pasword " + resetLink);

    }

    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid reset token"));

        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        user.setResetToken(null);
        userRepository.save(user);
    }

}