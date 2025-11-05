package com.bookfair.bookfairreservationsystembackend.dtos.response;

public record LoginResponse(
        String token,
        String username,
        String role
) {
}
