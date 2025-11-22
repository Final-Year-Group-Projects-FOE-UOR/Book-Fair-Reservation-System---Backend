package com.bookfair.bookfairreservationsystembackend.services.reservation;

import com.bookfair.bookfairreservationsystembackend.dtos.request.ReservationRequest;
import com.bookfair.bookfairreservationsystembackend.dtos.response.ReservationResponse;
import java.time.LocalDate;
import java.util.List;

public interface ReservationService {
    ReservationResponse createReservation(ReservationRequest request);

    // Admin APIs
    List<ReservationResponse> getAllReservations();

    List<ReservationResponse> filterReservations(String username, String business, String stallName, LocalDate date);

    boolean cancelReservation(Long id);

    ReservationResponse markCheckedIn(Long id);
}
