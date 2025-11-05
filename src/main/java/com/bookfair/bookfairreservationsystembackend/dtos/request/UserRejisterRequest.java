package com.bookfair.bookfairreservationsystembackend.dtos.request;

public record UserRejisterRequest(
        String email,
        String username,
        String password
) {
}
