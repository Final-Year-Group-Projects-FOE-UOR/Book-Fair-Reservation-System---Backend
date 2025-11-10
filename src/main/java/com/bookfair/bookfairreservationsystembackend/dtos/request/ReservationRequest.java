package com.bookfair.bookfairreservationsystembackend.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {
    private List<Integer> stallIds;
    private Integer userId; 
    private String userEmail;
}
