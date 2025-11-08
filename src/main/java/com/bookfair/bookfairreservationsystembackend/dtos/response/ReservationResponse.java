package com.bookfair.bookfairreservationsystembackend.dtos.response;

import java.time.LocalDateTime;
import java.util.List;

public class ReservationResponse {
    private List<Integer> reservationIds; // for batch creation
    private LocalDateTime reservationDate;
    private String status;
    private String message;

    public ReservationResponse(List<Integer> reservationIds, LocalDateTime reservationDate, String status,
            String message) {
        this.reservationIds = reservationIds;
        this.reservationDate = reservationDate;
        this.status = status;
        this.message = message;
    }

    public List<Integer> getReservationIds() {
        return reservationIds;
    }

    public LocalDateTime getReservationDate() {
        return reservationDate;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
