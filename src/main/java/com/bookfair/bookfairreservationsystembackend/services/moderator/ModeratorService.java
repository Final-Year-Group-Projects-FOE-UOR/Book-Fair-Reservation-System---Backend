package com.bookfair.bookfairreservationsystembackend.services.moderator;

import com.bookfair.bookfairreservationsystembackend.exception.BadRequestException;
import com.bookfair.bookfairreservationsystembackend.exception.NotFoundException;
import com.bookfair.bookfairreservationsystembackend.models.user.Role;
import com.bookfair.bookfairreservationsystembackend.models.user.User;
import com.bookfair.bookfairreservationsystembackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ModeratorService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    private static final String DEFAULT_MODERATOR_PASSWORD = "moderator123";

    // First-time login password change
    public User changePasswordFirstTime(String email, String newPassword) {
        User moderator = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Moderator not found"));

        if (moderator.getRole() != Role.ROLE_MODERATOR) {
            throw new BadRequestException("User is not a moderator");
        }

        if (!moderator.isFirstTimeLogin()) {
            throw new BadRequestException("Password has already been changed. Contact admin for reset.");
        }

        moderator.setPassword(passwordEncoder.encode(newPassword));
        moderator.setFirstTimeLogin(false);
        return userRepository.save(moderator);
    }

    // Normal password change is disabled for moderators
    public void updatePasswordBlocked() {
        throw new BadRequestException("Moderators cannot change password. Contact admin.");
    }


    // Admin resets password to default
    public User resetPasswordToDefault(String email) {
        User moderator = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Moderator not found"));

        if (moderator.getRole() != Role.ROLE_MODERATOR) {
            throw new BadRequestException("User is not a moderator");
        }

        moderator.setPassword(passwordEncoder.encode(DEFAULT_MODERATOR_PASSWORD));
        moderator.setFirstTimeLogin(true); // Force first-time login
        return userRepository.save(moderator);
    }
}
