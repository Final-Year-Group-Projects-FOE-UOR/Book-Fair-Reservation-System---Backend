package com.bookfair.bookfairreservationsystembackend.controllers.vendor;

import com.bookfair.bookfairreservationsystembackend.dtos.request.VendorRequest;
import com.bookfair.bookfairreservationsystembackend.dtos.response.ApiResponse;
import com.bookfair.bookfairreservationsystembackend.dtos.response.VendorResponse;
import com.bookfair.bookfairreservationsystembackend.services.vendor.VendorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/vendors")
@CrossOrigin(origins = "*")
public class VendorController {

    private final VendorService vendorService;

    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @PutMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse> updateVendor(@RequestParam String email, @RequestBody VendorRequest request) {
        VendorResponse response = vendorService.updateVendorByEmail(email, request);
        return ResponseEntity.ok(new ApiResponse(true, "Vendor updated successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getVendorByUserEmail(@RequestParam String email) {
        VendorResponse response = vendorService.getVendorByUserEmail(email);
        return ResponseEntity.ok(new ApiResponse(true, "Vendor fetched successfully", response));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getAllVendors() {
        List<VendorResponse> vendors = vendorService.getAllVendors();
        return ResponseEntity.ok(new ApiResponse(true, "All vendors fetched successfully", vendors));
    }
}
