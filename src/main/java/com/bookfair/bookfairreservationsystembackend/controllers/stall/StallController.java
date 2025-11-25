package com.bookfair.bookfairreservationsystembackend.controllers.stall;

import com.bookfair.bookfairreservationsystembackend.dtos.request.StallRequest;
import com.bookfair.bookfairreservationsystembackend.dtos.response.ApiResponse;
import com.bookfair.bookfairreservationsystembackend.dtos.response.StallResponse;
import com.bookfair.bookfairreservationsystembackend.models.stall.StallType;
import com.bookfair.bookfairreservationsystembackend.services.stall.StallService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/stalls")
public class StallController {

    private final StallService stallService;


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addStall(@RequestBody StallRequest request) {
        StallResponse response = stallService.addStall(request);
        return ResponseEntity.ok(new ApiResponse(true, "Stall added successfully", response));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteStall(@PathVariable Integer id) {
        stallService.deleteStall(id);
        return ResponseEntity.ok(new ApiResponse(true, "Stall deleted successfully", null));
    }


    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateStall(
            @PathVariable Integer id,
            @RequestBody StallRequest request
    ) {
        StallResponse response = stallService.updateStall(id, request);
        return ResponseEntity.ok(new ApiResponse(true, "Stall updated successfully", response));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllStalls() {
        List<StallResponse> stalls = stallService.getAllStalls();
        return ResponseEntity.ok(new ApiResponse(true, "All stalls fetched successfully", stalls));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    @GetMapping("/status")
    public ResponseEntity<ApiResponse> getStallsByAvailability(@RequestParam boolean available) {
        List<StallResponse> stalls = stallService.getStallsByAvailability(available);
        return ResponseEntity.ok(new ApiResponse(true, "Filtered stalls fetched", stalls));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    @PutMapping("/availability/{id}")
    public ResponseEntity<ApiResponse> updateAvailability(
            @PathVariable Integer id,
            @RequestParam boolean available
    ) {
        StallResponse response = stallService.updateAvailability(id, available);
        return ResponseEntity.ok(new ApiResponse(true, "Stall availability updated", response));
    }
    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    @GetMapping("/type")
    public ResponseEntity<ApiResponse> getStallsByType(@RequestParam String type) {
        List<StallResponse> stalls = stallService.getStallsByType(type);
        return ResponseEntity.ok(new ApiResponse(true, "Stalls fetched by type", stalls));
    }


}

//package com.bookfair.bookfairreservationsystembackend.controllers.stall;
//import com.bookfair.bookfairreservationsystembackend.dtos.request.StallRequest;
//import com.bookfair.bookfairreservationsystembackend.dtos.response.ApiResponse;
//import com.bookfair.bookfairreservationsystembackend.dtos.response.StallResponse;
//import com.bookfair.bookfairreservationsystembackend.services.stall.StallService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//import java.util.List;
//
//@RequiredArgsConstructor
//@RestController
//@RequestMapping("${api.prefix}/admin/stalls")
//public class StallController {
//
//    private final  StallService stallService;
//
//    @PreAuthorize("hasRole('ADMIN')")
//    @PostMapping("/add")
//    public ResponseEntity<ApiResponse> addStall(@RequestBody StallRequest request) {
//        StallResponse response = stallService.addStall(request);
//        return ResponseEntity.ok(new ApiResponse(true, "Stall added successfully", response));
//    }
//
//    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
//    @PutMapping("/update/{id}")
//    public ResponseEntity<ApiResponse> updateStall(
//            @PathVariable Integer id,
//            @RequestBody StallRequest request
//    ) {
//        StallResponse response = stallService.updateStall(id, request);
//        return ResponseEntity.ok(new ApiResponse(true, "Stall updated successfully", response));
//    }
//
//    @PreAuthorize("hasRole('ADMIN')")
//    @DeleteMapping("/delete/{id}")
//    public ResponseEntity<ApiResponse> deleteStall(@PathVariable Integer id) {
//        stallService.deleteStall(id);
//        return ResponseEntity.ok(new ApiResponse(true, "Stall deleted successfully", null));
//    }
//
//    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
//    @GetMapping("/all")
//    public ResponseEntity<ApiResponse> getAllStalls() {
//        List<StallResponse> stalls=stallService.getAllStalls();
//        return ResponseEntity.ok(new ApiResponse(true, "All stalls fetched successfully", stalls));
//    }
//
//    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
//    @GetMapping("/status")
//    public ResponseEntity<ApiResponse> getStallsByAvailability(@RequestParam boolean available) {
//        List<StallResponse> stalls = stallService.getStallsByAvailability(available);
//        return ResponseEntity.ok(new ApiResponse(true, "Filtered stalls fetched", stalls));
//    }
//    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
//    @PutMapping("/availability/{id}")
//    public ResponseEntity<ApiResponse> updateAvailability(
//            @PathVariable Integer id,
//            @RequestParam boolean available
//    ) {
//        StallResponse response = stallService.updateAvailability(id, available);
//        return ResponseEntity.ok(new ApiResponse(true, "Stall availability updated", response));
//    }
//
//}
