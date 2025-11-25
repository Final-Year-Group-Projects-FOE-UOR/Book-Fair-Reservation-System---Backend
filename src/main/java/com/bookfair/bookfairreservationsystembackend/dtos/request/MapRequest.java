package com.bookfair.bookfairreservationsystembackend.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MapRequest {
    private String mapUrl;
    private String description;
}
