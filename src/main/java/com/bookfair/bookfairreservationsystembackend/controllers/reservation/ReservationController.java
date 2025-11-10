package com.bookfair.bookfairreservationsystembackend.controllers.reservation;

import com.bookfair.bookfairreservationsystembackend.dtos.request.ReservationRequest;
import com.bookfair.bookfairreservationsystembackend.dtos.response.ApiResponse;
import com.bookfair.bookfairreservationsystembackend.dtos.response.ReservationResponse;
import com.bookfair.bookfairreservationsystembackend.services.reservation.ReservationService;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody ReservationRequest request) {
        ReservationResponse response = reservationService.createReservation(request);
        return ResponseEntity.ok(response);
    }

   
    @GetMapping
    public ResponseEntity<ApiResponse> getAllReservations() {
        List<ReservationResponse> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(new ApiResponse(true, "All reservations fetched", reservations));
    }

        @DeleteMapping("/cancel/{id}")
    public ResponseEntity<ApiResponse> cancelReservation(@PathVariable Long id) {
        boolean canceled = reservationService.cancelReservation(id);
        return ResponseEntity.ok(new ApiResponse(true, "Reservation canceled", canceled));
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
}