package com.bookfair.bookfairreservationsystembackend.controllers.reservation;

import com.bookfair.bookfairreservationsystembackend.dtos.response.ApiResponse;
import com.bookfair.bookfairreservationsystembackend.dtos.response.ReservationResponse;
import com.bookfair.bookfairreservationsystembackend.services.reservation.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/admin/reservations")
@RequiredArgsConstructor
public class AdminReservationController {

    private final ReservationService reservationService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllReservations() {
        List<ReservationResponse> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(new ApiResponse(true, "All reservations fetched", reservations));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse> filterReservations(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String business,
            @RequestParam(required = false) String stallName,
            @RequestParam(required = false) String date) {
        LocalDate reservationDate = (date != null) ? LocalDate.parse(date) : null;
        List<ReservationResponse> reservations = reservationService.filterReservations(username, business, stallName,
                reservationDate);
        return ResponseEntity.ok(new ApiResponse(true, "Filtered reservations fetched", reservations));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<ApiResponse> cancelReservation(@PathVariable Long id) {
        boolean canceled = reservationService.cancelReservation(id);
        return ResponseEntity.ok(new ApiResponse(true, "Reservation canceled", canceled));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @PutMapping("/checkin/{id}")
    public ResponseEntity<ApiResponse> markCheckedIn(@PathVariable Long id) {
        ReservationResponse response = reservationService.markCheckedIn(id);
        return ResponseEntity.ok(new ApiResponse(true, "Reservation checked-in", response));
    }
}
