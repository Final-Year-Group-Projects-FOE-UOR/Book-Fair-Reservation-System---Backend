package com.bookfair.bookfairreservationsystembackend.dtos.response;

import com.bookfair.bookfairreservationsystembackend.models.stall.StallType;
public record StallResponse (
    Integer id,
    String stallName,
    StallType type,
    double price,
    String dimension,
    String locationCode,
    Boolean available ){
}
