package com.bookfair.bookfairreservationsystembackend.repositories;
import com.bookfair.bookfairreservationsystembackend.models.stall.Stall;
import com.bookfair.bookfairreservationsystembackend.models.stall.StallType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StallRepository extends JpaRepository<Stall,Integer> {
    Optional<Stall> findByStallName(String stallName);
    boolean existsByStallName(String stallName);
    List<Stall> findByType(StallType type);
}
