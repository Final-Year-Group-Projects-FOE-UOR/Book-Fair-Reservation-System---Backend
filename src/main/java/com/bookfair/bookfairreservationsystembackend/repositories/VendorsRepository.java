package com.bookfair.bookfairreservationsystembackend.repositories;

import com.bookfair.bookfairreservationsystembackend.models.vendor.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VendorsRepository extends JpaRepository<Vendor, Integer> {
    Optional<Vendor> findByUserId(Integer userId);

    Optional<Vendor> findByUserEmail(String userEmail);
}
