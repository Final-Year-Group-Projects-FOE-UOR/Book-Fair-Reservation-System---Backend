package com.bookfair.bookfairreservationsystembackend.repositories;
import com.bookfair.bookfairreservationsystembackend.models.reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserId(Long userId);
    List<Reservation> findByStallId(Long stallId);
    List<Reservation> findByReservationDate(LocalDate reservationDate);
}
