package com.bookfair.bookfairreservationsystembackend.dtos.response;
import java.time.LocalDate;

public record ReservationResponse(
        Long id,
        String userName,
        String stallName,
        LocalDate reservationDate,
        boolean checkedIn
){
}
