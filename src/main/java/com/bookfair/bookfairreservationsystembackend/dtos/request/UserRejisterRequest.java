package com.bookfair.bookfairreservationsystembackend.dtos.request;

public record UserRejisterRequest(
        String username,
        String password
) {
}
