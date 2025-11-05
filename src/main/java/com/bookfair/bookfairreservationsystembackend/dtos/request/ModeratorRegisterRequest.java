package com.bookfair.bookfairreservationsystembackend.dtos.request;

public record  ModeratorRegisterRequest(
        String email,
        String username,
        String password
)
{
}
