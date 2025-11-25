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
import com.bookfair.bookfairreservationsystembackend.services.EmailService;
import com.bookfair.bookfairreservationsystembackend.services.QRCodeService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationServiceImplement implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final StallRepository stallRepository;
    private final QRCodeService qrCodeService;
    private final EmailService emailService;

    public ReservationServiceImplement(ReservationRepository reservationRepository, UserRepository userRepository,
            StallRepository stallRepository, QRCodeService qrCodeService, EmailService emailService) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.stallRepository = stallRepository;
        this.qrCodeService = qrCodeService;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {
        User user = null;
        if (request.getUserEmail() != null) {
            user = userRepository.findByEmail(request.getUserEmail()).orElse(null);
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

        List<Integer> createdStallIds = new ArrayList<>();
        List<String> stallNames = new ArrayList<>();
        Reservation lastReservation = null;

        if (request.getStallIds() == null || request.getStallIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one stallId must be provided");
        }

        LocalDateTime reservationTime = LocalDateTime.now();

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

            Reservation reservation = new Reservation();
            reservation.setUser(user);
            reservation.setUserEmail(user.getEmail());
            reservation.setStall(stall);
            reservation.setReservationDate(reservationTime);
            reservation.setStatus(ReservationStatus.CONFIRMED);

            lastReservation = reservationRepository.save(reservation);
            createdStallIds.add(stall.getId());
            stallNames.add(stall.getStallName());

            String qrData = String.format(
                    "{\"reservationId\":%d,\"userEmail\":\"%s\",\"stallId\":%d,\"stallName\":\"%s\",\"date\":\"%s\",\"status\":\"%s\"}",
                    reservation.getId(),
                    user.getEmail(),
                    stall.getId(),
                    stall.getStallName(),
                    reservationTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    ReservationStatus.CONFIRMED.name());

            String qrCodePath = qrCodeService.generateQRCode(qrData, "reservation_" + reservation.getId());
            reservation.setQrCodePath(qrCodePath);
            reservationRepository.save(reservation);

            stall.setAvailable(false);
            stallRepository.save(stall);
        }

        StringBuilder stallIdsJson = new StringBuilder();
        for (int i = 0; i < createdStallIds.size(); i++) {
            if (i > 0)
                stallIdsJson.append(",");
            stallIdsJson.append(createdStallIds.get(i));
        }

        StringBuilder stallNamesJson = new StringBuilder();
        for (int i = 0; i < stallNames.size(); i++) {
            if (i > 0)
                stallNamesJson.append(",");
            stallNamesJson.append("\"").append(stallNames.get(i)).append("\"");
        }

        String qrData = String.format(
                "{\"userEmail\":\"%s\",\"stallIds\":[%s],\"stallNames\":[%s],\"date\":\"%s\",\"status\":\"%s\"}",
                user.getEmail(),
                stallIdsJson.toString(),
                stallNamesJson.toString(),
                reservationTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                ReservationStatus.CONFIRMED.name());

        byte[] qrCodeBytes = qrCodeService.generateQRCodeBytes(qrData);

        String emailSubject = "Booking Confirmation - Book Fair Reservation";
        String emailBody = "<html><body>" +
                "<h2>Reservation Confirmed!</h2>" +
                "<p>Dear " + user.getUsername() + ",</p>" +
                "<p>Your reservation has been successfully confirmed.</p>" +
                "<p><strong>Reservation Details:</strong></p>" +
                "<ul>" +
                "<li>Stalls: " + String.join(", ", stallNames) + "</li>" +
                "<li>Date: " + reservationTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "</li>" +
                "<li>Status: " + ReservationStatus.CONFIRMED.name() + "</li>" +
                "</ul>" +
                "<p>Please find your QR code attached. Show this QR code at the venue for entry.</p>" +
                "<p>Thank you for your reservation!</p>" +
                "</body></html>";

        emailService.sendEmailWithAttachment(user.getEmail(), emailSubject, emailBody, qrCodeBytes,
                "reservation-qr.png");

        return new ReservationResponse(
                lastReservation.getId(),
                createdStallIds,
                reservationTime,
                ReservationStatus.CONFIRMED.name(),
                "Reservation created successfully",
                user.getEmail());
    }

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

    @Override
    public List<ReservationResponse> getReservationsByUserEmail(String userEmail) {
        List<Reservation> reservations = reservationRepository.findByUserEmail(userEmail);
        return reservations.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ReservationResponse toResponse(Reservation reservation) {
        List<Integer> stallIds = new ArrayList<>();
        if (reservation.getStall() != null && reservation.getStall().getId() != null) {
            stallIds.add(reservation.getStall().getId());
        }

        String uEmail = reservation.getUserEmail() != null ? reservation.getUserEmail()
                : (reservation.getUser() != null ? reservation.getUser().getEmail() : null);

        return new ReservationResponse(
                reservation.getId(),
                stallIds,
                reservation.getReservationDate(),
                reservation.getStatus() != null ? reservation.getStatus().name() : null,
                null,
                uEmail);
    }
}
