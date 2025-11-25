package com.bookfair.bookfairreservationsystembackend.dtos.response;

import com.bookfair.bookfairreservationsystembackend.models.user.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserValidationResponse {
    private String username;
    private String email;
    private Role role;
    private boolean isActive;
}
