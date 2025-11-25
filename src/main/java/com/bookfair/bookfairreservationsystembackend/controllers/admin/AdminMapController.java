package com.bookfair.bookfairreservationsystembackend.controllers.admin;

import com.bookfair.bookfairreservationsystembackend.dtos.request.MapRequest;
import com.bookfair.bookfairreservationsystembackend.dtos.response.ApiResponse;
import com.bookfair.bookfairreservationsystembackend.dtos.response.MapResponse;
import com.bookfair.bookfairreservationsystembackend.models.stall.BookfairMap;
import com.bookfair.bookfairreservationsystembackend.services.admin.AdminMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/admin/map")
public class AdminMapController {

    private final AdminMapService adminMapService;

    @PostMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> addOrUpdateMap(@RequestBody MapRequest request) {
        MapResponse response = adminMapService.addOrUpdateMap(request);
        return ResponseEntity.ok(new ApiResponse(true, "Map updated successfully", response));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getMap() {
        MapResponse response = adminMapService.getMap();
        return ResponseEntity.ok(new ApiResponse(true, "Map fetched successfully", response));
    }

}
