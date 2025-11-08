package com.bookfair.bookfairreservationsystembackend.services.reservation;

import com.bookfair.bookfairreservationsystembackend.dtos.request.ReservationRequest;
import com.bookfair.bookfairreservationsystembackend.dtos.response.ReservationResponse;
import com.bookfair.bookfairreservationsystembackend.models.reservation.Reservation;
import com.bookfair.bookfairreservationsystembackend.models.reservation.ReservationStatus;
import com.bookfair.bookfairreservationsystembackend.models.stall.Stall;
import com.bookfair.bookfairreservationsystembackend.models.user.User;
import com.bookfair.bookfairreservationsystembackend.repositories.ReservationRepository;
import com.bookfair.bookfairreservationsystembackend.repositories.StallRepository;
import com.bookfair.bookfairreservationsystembackend.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationServiceImplement implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final StallRepository stallRepository;

    public ReservationServiceImplement(ReservationRepository reservationRepository, UserRepository userRepository,
            StallRepository stallRepository) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.stallRepository = stallRepository;
    }

    @Override
    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {
        // Lookup user by email (preferred) or by ID fallback
        User user = null;
        if (request.getUserEmail() != null) {
            user = userRepository.findByEmail(request.getUserEmail());
        }
        if (user == null && request.getUserId() != null) {
            user = userRepository.findById(request.getUserId()).orElse(null);
        }
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        List<Reservation> existing = reservationRepository.findByUserId(user.getId());
        if (existing.size() >= 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "User has reached the maximum reservation limit (3 stalls).");
        }

        List<Integer> createdIds = new ArrayList<>();

        if (request.getStallIds() == null || request.getStallIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one stallId must be provided");
        }

        for (Integer stallId : request.getStallIds()) {
            if (stallId == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Null stallId encountered");
            }
            Stall stall = stallRepository.findById(stallId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Stall not found with ID: " + stallId));

            if (!stall.isAvailable()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Stall " + stall.getStallName() + " is already reserved.");
            }

            // Create new reservation
            Reservation reservation = new Reservation();
            reservation.setUser(user);
            reservation.setStall(stall);
            reservation.setReservationDate(LocalDateTime.now());
            reservation.setStatus(ReservationStatus.PENDING);

            // Save reservation
            Reservation saved = reservationRepository.save(reservation);
            createdIds.add(saved.getId());

            // Mark stall as reserved
            stall.setAvailable(false);
            stallRepository.save(stall);
        }

        return new ReservationResponse(
                createdIds,
                LocalDateTime.now(),
                ReservationStatus.PENDING.name(),
                "Reservation created successfully and pending confirmation.");
    }

    // ------- Admin operations -------
    @Override
    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationResponse> filterReservations(String username, String business, String stallName,
            LocalDate date) {
        return reservationRepository.findAll().stream()
                .filter(r -> username == null
                        || (r.getUser() != null && username.equalsIgnoreCase(r.getUser().getUsername())))
                // business filter not supported by current model; ignore if provided
                .filter(r -> stallName == null
                        || (r.getStall() != null && stallName.equalsIgnoreCase(r.getStall().getStallName())))
                .filter(r -> date == null
                        || (r.getReservationDate() != null && r.getReservationDate().toLocalDate().isEqual(date)))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public boolean cancelReservation(Long id) {
        if (id == null)
            return false;
        return reservationRepository.findById(id.intValue())
                .map(res -> {
                    res.setStatus(ReservationStatus.CANCELLED);
                    // free the stall if present
                    if (res.getStall() != null) {
                        res.getStall().setAvailable(true);
                        stallRepository.save(res.getStall());
                    }
                    reservationRepository.save(res);
                    return true;
                }).orElse(false);
    }

    @Override
    public ReservationResponse markCheckedIn(Long id) {
        if (id == null)
            throw new RuntimeException("Reservation id required");
        Reservation res = reservationRepository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + id));
        res.setCheckedIn(true);
        Reservation saved = reservationRepository.save(res);
        return toResponse(saved);
    }

    private ReservationResponse toResponse(Reservation reservation) {
        List<Integer> oneId = new ArrayList<>();
        if (reservation.getId() != null) {
            oneId.add(reservation.getId());
        }
        return new ReservationResponse(
                oneId,
                reservation.getReservationDate(),
                reservation.getStatus() != null ? reservation.getStatus().name() : null,
                null);
    }
}
