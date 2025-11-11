package com.bookfair.bookfairreservationsystembackend.controllers.moderator;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping("/api/v2/moderator")
@RequestMapping("${api.prefix}/moderator")
public class ModeratorController {

    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @GetMapping("/manage-stalls")
    public String manageStalls() {
        return "Servant/Admin can manage stalls.";
    }
}
