package com.bookfair.bookfairreservationsystembackend.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MapResponse {
    private Integer id;
    private String mapUrl;
    private String description;
    private LocalDateTime createdAt;
    private boolean active;
}
