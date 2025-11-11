package com.bookfair.bookfairreservationsystembackend.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import com.bookfair.bookfairreservationsystembackend.models.user.Role;
import com.bookfair.bookfairreservationsystembackend.models.user.User;
import java.util.List;
import java.util.Optional;
public interface UserRepository extends JpaRepository<User,Integer> {
    User findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRole(Role role);
    Optional<User>findByResetToken(String resetToken);
}