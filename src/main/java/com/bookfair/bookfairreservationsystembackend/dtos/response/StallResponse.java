package com.bookfair.bookfairreservationsystembackend.dtos.response;

import com.bookfair.bookfairreservationsystembackend.models.stall.Location;
import com.bookfair.bookfairreservationsystembackend.models.stall.MapMetadata;
import com.bookfair.bookfairreservationsystembackend.models.stall.StallType;
public record StallResponse (
    Integer id,
    String stallName,
    StallType type,
    double price,
    String dimension,
    MapMetadata mapMetadata,
    Boolean available
    ){
}
