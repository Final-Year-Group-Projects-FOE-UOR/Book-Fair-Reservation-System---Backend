package com.bookfair.bookfairreservationsystembackend.dtos.response;

public record ApiResponse (
        boolean success,
        String message,
        Object data
){}