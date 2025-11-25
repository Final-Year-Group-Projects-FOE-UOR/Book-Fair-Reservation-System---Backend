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

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllReservations() {
        List<ReservationResponse> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(new ApiResponse(true, "All reservations fetched", reservations));
    }

    @GetMapping()
    public ResponseEntity<ApiResponse> getReservationsByUser(@RequestParam String email) {
        List<ReservationResponse> reservations = reservationService.getReservationsByUserEmail(email);
        if (reservations.isEmpty()) {
            return ResponseEntity.ok(new ApiResponse(false, "No reservations found for the user", null));
        }
        return ResponseEntity.ok(new ApiResponse(true, "User reservations fetched successfully", reservations));
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse> getPendingReservations() {
        List<ReservationResponse> pendingReservations = reservationService.getPendingReservations();
        if (pendingReservations.isEmpty()) {
            return ResponseEntity.ok(new ApiResponse(true, "No pending reservations found", null));
        }
        return ResponseEntity
                .ok(new ApiResponse(true, "Pending reservations fetched successfully", pendingReservations));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/approve/{id}")
    public ResponseEntity<ApiResponse> approveReservation(@PathVariable Long id) {
        try {
            ReservationResponse approvedReservation = reservationService.approveReservation(id);
            return ResponseEntity.ok(new ApiResponse(true, "Reservation approved successfully", approvedReservation));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Failed to approve reservation: " + e.getMessage(), null));
        }
    }
}