package com.bookfair.bookfairreservationsystembackend.dtos.response;

public record LoginResponse(
        String token,
        String email,
        String role
) {
}
