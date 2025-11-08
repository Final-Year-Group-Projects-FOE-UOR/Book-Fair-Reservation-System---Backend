
package com.bookfair.bookfairreservationsystembackend.config;

import com.bookfair.bookfairreservationsystembackend.models.user.Role;
import com.bookfair.bookfairreservationsystembackend.models.user.User;
import com.bookfair.bookfairreservationsystembackend.models.stall.Stall;
import com.bookfair.bookfairreservationsystembackend.models.stall.StallType;
import com.bookfair.bookfairreservationsystembackend.repositories.UserRepository;
import com.bookfair.bookfairreservationsystembackend.repositories.StallRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StallRepository stallRepository;

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail("admin@gmail.com") == null) {
            User admin = new User();
            admin.setEmail("admin@gmail.com");
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ROLE_ADMIN);
            admin.setActive(true);
            userRepository.save(admin);
            System.out.println("Default admin created (username: admin,email:admin@gmail.com, password: admin123)");
        }

        // Seed a few stalls if none exist yet
        if (stallRepository.count() == 0) {
            Stall s1 = new Stall();
            s1.setStallName("A-1");
            s1.setType(StallType.SMALL);
            s1.setPrice(1000.0);
            s1.setDimensions("2x2m");
            s1.setLocationCode("LOC-A1");
            s1.setAvailable(true);

            Stall s2 = new Stall();
            s2.setStallName("A-2");
            s2.setType(StallType.MEDIUM);
            s2.setPrice(1500.0);
            s2.setDimensions("3x2m");
            s2.setLocationCode("LOC-A2");
            s2.setAvailable(true);

            Stall s3 = new Stall();
            s3.setStallName("B-1");
            s3.setType(StallType.LARGE);
            s3.setPrice(2000.0);
            s3.setDimensions("4x3m");
            s3.setLocationCode("LOC-B1");
            s3.setAvailable(true);

            stallRepository.save(s1);
            stallRepository.save(s2);
            stallRepository.save(s3);
            System.out.println("Seeded default stalls: A-1, A-2, B-1");
        }
    }
}
