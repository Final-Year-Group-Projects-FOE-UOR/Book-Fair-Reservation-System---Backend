package com.bookfair.bookfairreservationsystembackend.dtos.response;

public record UserResponse(
        Integer id,
        String username,
        String role
) {
}
