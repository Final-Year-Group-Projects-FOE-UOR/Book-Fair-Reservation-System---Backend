package com.bookfair.bookfairreservationsystembackend.services.reservation;

import com.bookfair.bookfairreservationsystembackend.dtos.response.ReservationResponse;
import com.bookfair.bookfairreservationsystembackend.exception.NotFoundException;
import com.bookfair.bookfairreservationsystembackend.models.reservation.Reservation;
import com.bookfair.bookfairreservationsystembackend.repositories.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private  final ReservationRepository reservationRepository;

    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ReservationResponse> filterReservations(String username, String business, String stallName, LocalDate date) {
        return reservationRepository.findAll()
                .stream()
                .filter(r -> username == null || r.getUser().getUsername().equalsIgnoreCase(username))
                .filter(r -> stallName == null || r.getStall().getStallName().equalsIgnoreCase(stallName))
                .filter(r -> date == null || r.getReservationDate().isEqual(date))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    public boolean cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Reservation not found: " + reservationId));
        reservationRepository.delete(reservation);
        return true;
    }
    public ReservationResponse markCheckedIn(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Reservation not found: " + reservationId));
        reservation.setCheckedIn(true);
        Reservation updated = reservationRepository.save(reservation);
        return mapToResponse(updated);
    }

    private ReservationResponse mapToResponse(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getUser().getUsername(),
                reservation.getStall().getStallName(),
                reservation.getReservationDate(),
                reservation.isCheckedIn()
        );
    }


}
