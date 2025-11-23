package com.bookfair.bookfairreservationsystembackend.dtos.request;

public record FirstTimePasswordChangeRequest(
        String email,
        String newPassword

) {}

