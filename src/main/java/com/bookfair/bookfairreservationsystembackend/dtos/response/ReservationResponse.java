package com.bookfair.bookfairreservationsystembackend.dtos.response;

import java.time.LocalDateTime;
import java.util.List;

public class ReservationResponse {
    private Integer id;
    private List<Integer> stallIds;
    private LocalDateTime reservationDate;
    private String status;
    private String message;

    public ReservationResponse(Integer id, List<Integer> stallIds, LocalDateTime reservationDate, String status,
            String message) {
        this.id = id;
        this.stallIds = stallIds;
        this.reservationDate = reservationDate;
        this.status = status;
        this.message = message;
    }

    public Integer getId() {
        return id;
    }

    public List<Integer> getStallIds() {
        return stallIds;
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
