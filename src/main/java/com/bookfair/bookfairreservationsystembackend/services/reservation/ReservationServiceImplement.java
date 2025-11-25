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
            reservation.setStatus(ReservationStatus.PENDING);

            lastReservation = reservationRepository.save(reservation);
            createdStallIds.add(stall.getId());
            stallNames.add(stall.getStallName());

        }

        String emailSubject = "Reservation Request Submitted - Book Fair 2025";
        String emailBody = "<html><body>" +
                "<h1>Book Fair 2025</h1>" +
                "<h2>Reservation Request Submitted</h2>" +
                "<p>Dear " + user.getUsername() + ",</p>" +
                "<p>Thank you for submitting your stall reservation request for the Book Fair 2025. " +
                "Your request has been received and is currently under review by our administrative team.</p>" +
                "<h3>Reservation Details</h3>" +
                "<p><strong>Reservation ID:</strong> #" + (lastReservation != null ? lastReservation.getId() : "TBD")
                + "</p>" +
                "<p><strong>Requested Stalls:</strong> " + String.join(", ", stallNames) + "</p>" +
                "<p><strong>Number of Stalls:</strong> " + stallNames.size() + "</p>" +
                "<p><strong>Submission Date:</strong> "
                + reservationTime.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a")) + "</p>" +
                "<p><strong>Current Status:</strong> PENDING APPROVAL</p>" +
                "<h3>What happens next?</h3>" +
                "<ol>" +
                "<li>Our team will review your reservation request</li>" +
                "<li>You will receive an email notification once your request is processed</li>" +
                "<li>If approved, you'll receive your QR code for venue entry</li>" +
                "<li>If additional information is needed, we'll contact you directly</li>" +
                "</ol>" +
                "<p><strong>Important:</strong> Please keep this email for your records. " +
                "Your reservation is not confirmed until you receive approval notification.</p>" +
                "<p>If you have any questions or need to make changes to your reservation, please contact our support team immediately.</p>"
                +
                "<p>Thank you for participating in Book Fair 2025!</p>" +
                "<p><em>This is an automated message. Please do not reply to this email.</em></p>" +
                "</body></html>";

        emailService.sendEmail(user.getEmail(), emailSubject, emailBody);

        return new ReservationResponse(
                lastReservation.getId(),
                createdStallIds,
                reservationTime,
                ReservationStatus.PENDING.name(),
                "Reservation submitted successfully and is pending approval",
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

    @Override
    public List<ReservationResponse> getPendingReservations() {
        List<Reservation> pendingReservations = reservationRepository.findByStatus(ReservationStatus.PENDING);
        return pendingReservations.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReservationResponse approveReservation(Long id) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reservation ID is required");
        }

        Reservation reservation = reservationRepository.findById(id.intValue())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Reservation not found with ID: " + id));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Only pending reservations can be approved");
        }

        reservation.setStatus(ReservationStatus.CONFIRMED);

        String qrData = null;
        byte[] qrCodeBytes = null;

        try {
            String userEmail = "unknown";
            if (reservation.getUserEmail() != null && !reservation.getUserEmail().trim().isEmpty()) {
                userEmail = reservation.getUserEmail();
            } else if (reservation.getUser() != null && reservation.getUser().getEmail() != null) {
                userEmail = reservation.getUser().getEmail();
            }

            String stallName = "unknown";
            Integer stallId = 0;
            if (reservation.getStall() != null) {
                if (reservation.getStall().getStallName() != null
                        && !reservation.getStall().getStallName().trim().isEmpty()) {
                    stallName = reservation.getStall().getStallName();
                }
                if (reservation.getStall().getId() != null) {
                    stallId = reservation.getStall().getId();
                }
            }

            String dateStr = "unknown";
            if (reservation.getReservationDate() != null) {
                dateStr = reservation.getReservationDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } else {
                dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }

            qrData = String.format(
                    "{\"reservationId\":%d,\"userEmail\":\"%s\",\"stallId\":%d,\"stallName\":\"%s\",\"date\":\"%s\",\"status\":\"%s\"}",
                    reservation.getId(),
                    userEmail,
                    stallId,
                    stallName,
                    dateStr,
                    ReservationStatus.CONFIRMED.name());

            qrCodeBytes = qrCodeService.generateQRCodeBytes(qrData);

            if (reservation.getQrCodePath() == null || reservation.getQrCodePath().isEmpty()) {
                String qrCodePath = qrCodeService.generateQRCode(qrData, "reservation_" + reservation.getId());
                reservation.setQrCodePath(qrCodePath);
            }
        } catch (Exception e) {
            System.err.println(
                    "Failed to generate QR code for reservation " + reservation.getId() + ": " + e.getMessage());
            if (reservation.getQrCodePath() == null || reservation.getQrCodePath().isEmpty()) {
                reservation.setQrCodePath("qr_generation_failed_" + reservation.getId());
            }
        }

        if (reservation.getStall() != null) {
            reservation.getStall().setAvailable(false);
            stallRepository.save(reservation.getStall());
        }

        Reservation savedReservation = reservationRepository.save(reservation);

        if (reservation.getUser() != null && reservation.getUserEmail() != null &&
                !reservation.getUserEmail().trim().isEmpty()) {
            try {
                String username = (reservation.getUser().getUsername() != null)
                        ? reservation.getUser().getUsername()
                        : "User";
                String stallName = (reservation.getStall() != null && reservation.getStall().getStallName() != null)
                        ? reservation.getStall().getStallName()
                        : "Unknown Stall";
                String emailSubject = "Reservation Approved - Book Fair 2025";
                String emailBody = "<html><body>" +
                        "<h1>Book Fair 2025</h1>" +
                        "<h2>Reservation Approved!</h2>" +
                        "<p>Dear " + username + ",</p>" +
                        "<p>Great news! Your stall reservation request has been approved and confirmed by our administrative team. "
                        +
                        "You can now proceed with your preparations for the Book Fair 2025.</p>" +
                        "<h3>Confirmed Reservation Details</h3>" +
                        "<p><strong>Reservation ID:</strong> #" + reservation.getId() + "</p>" +
                        "<p><strong>Stall:</strong> " + stallName + "</p>" +
                        "<p><strong>Reservation Date:</strong> "
                        + reservation.getReservationDate()
                                .format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a"))
                        + "</p>" +
                        "<p><strong>Current Status:</strong> CONFIRMED</p>" +
                        "<h3>Important Information</h3>" +
                        "<ul>" +
                        "<li>Your QR code has been generated and is ready for use</li>" +
                        "<li>Please show your QR code at the venue entrance for entry</li>" +
                        "<li>Arrive at your designated stall area on time</li>" +
                        "<li>Keep this confirmation email for your records</li>" +
                        "</ul>" +
                        "<p>If you have any questions or need assistance, please contact our support team.</p>" +
                        "<p>Thank you for participating in Book Fair 2025! We look forward to seeing you at the event.</p>"
                        +
                        "<p><em>This is an automated message. Please do not reply to this email.</em></p>" +
                        "</body></html>";

                if (qrCodeBytes != null) {
                    emailService.sendEmailWithAttachment(reservation.getUserEmail(), emailSubject, emailBody,
                            qrCodeBytes, "reservation-qr-code.png");
                    System.out.println(
                            "Approval email sent with QR code attachment for reservation ID: " + reservation.getId());
                } else {
                    emailService.sendEmail(reservation.getUserEmail(), emailSubject, emailBody);
                    System.out.println("Approval email sent without QR code attachment for reservation ID: "
                            + reservation.getId());
                }
            } catch (Exception e) {
                System.err.println("Failed to send approval email: " + e.getMessage());
            }
        }

        return toResponse(savedReservation);
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
