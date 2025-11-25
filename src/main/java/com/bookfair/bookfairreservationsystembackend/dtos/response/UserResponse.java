package com.bookfair.bookfairreservationsystembackend.dtos.response;

public record UserResponse(
        Integer id,
        String email,
        String username,
        String role
) {
}
