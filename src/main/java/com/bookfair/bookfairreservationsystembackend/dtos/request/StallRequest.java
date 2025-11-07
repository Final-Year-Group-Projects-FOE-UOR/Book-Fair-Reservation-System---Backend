package com.bookfair.bookfairreservationsystembackend.dtos.request;

import com.bookfair.bookfairreservationsystembackend.models.StallType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class StallRequest {

    @NotBlank(message = "Stall name cannot be blank")
    private String stallName;

    @NotNull(message = "Stall type cannot be null")
    private StallType type;

    @Positive(message = "Price must be positive")
    private double price;

    @NotBlank(message = "Dimensions cannot be blank")
    private String dimensions;

    @NotBlank(message = "Location code cannot be blank")
    private  String locationCode;



}
