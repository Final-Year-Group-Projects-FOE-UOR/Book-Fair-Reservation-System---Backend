package com.bookfair.bookfairreservationsystembackend.repositories;

import com.bookfair.bookfairreservationsystembackend.models.reservation.Reservation;
import com.bookfair.bookfairreservationsystembackend.models.reservation.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    List<Reservation> findByUserId(Integer userId);

    List<Reservation> findByUserEmail(String userEmail);

    List<Reservation> findByStatus(ReservationStatus status);
}
