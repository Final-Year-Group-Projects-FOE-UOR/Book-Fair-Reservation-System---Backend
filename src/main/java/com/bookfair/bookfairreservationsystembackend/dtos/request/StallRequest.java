package com.bookfair.bookfairreservationsystembackend.dtos.request;

import com.bookfair.bookfairreservationsystembackend.models.stall.Location;
import com.bookfair.bookfairreservationsystembackend.models.stall.MapMetadata;
import com.bookfair.bookfairreservationsystembackend.models.stall.StallType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record StallRequest (

        @NotBlank(message = "Stall name cannot be blank")
        String stallName,
        @NotNull(message = "Stall type cannot be null")
        StallType type,
        @Positive(message = "Price must be positive")
        double price,
        @NotBlank(message = "Dimensions cannot be blank")
        String dimensions,
        @NotBlank(message = "Location code cannot be blank")
        MapMetadata mapMetadata


){}
