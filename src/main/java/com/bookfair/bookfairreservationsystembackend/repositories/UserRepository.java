package com.bookfair.bookfairreservationsystembackend.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import com.bookfair.bookfairreservationsystembackend.models.User;
public interface UserRepository extends JpaRepository<User,Integer> {
    User findByUsername(String username);
}
