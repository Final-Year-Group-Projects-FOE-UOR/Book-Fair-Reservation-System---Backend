package com.bookfair.bookfairreservationsystembackend.services.vendor;

import com.bookfair.bookfairreservationsystembackend.dtos.request.VendorRequest;
import com.bookfair.bookfairreservationsystembackend.dtos.response.VendorResponse;
import com.bookfair.bookfairreservationsystembackend.models.vendor.Vendor;
import com.bookfair.bookfairreservationsystembackend.repositories.VendorsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class VendorServiceImplement implements VendorService {

    private final VendorsRepository vendorsRepository;

    public VendorServiceImplement(VendorsRepository vendorsRepository) {
        this.vendorsRepository = vendorsRepository;
    }

    @Override
    public VendorResponse updateVendorByEmail(String userEmail, VendorRequest request) {
        Vendor vendor = vendorsRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Vendor not found for user email: " + userEmail));

        vendor.setBusinessName(request.getBusinessName() != null ? request.getBusinessName() : "");
        vendor.setGenres(request.getGenres() != null ? request.getGenres() : new java.util.ArrayList<>());

        Vendor updatedVendor = vendorsRepository.save(vendor);
        return toResponse(updatedVendor);
    }

    @Override
    @Transactional(readOnly = true)
    public VendorResponse getVendorByUserEmail(String userEmail) {
        Vendor vendor = vendorsRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Vendor not found for user email: " + userEmail));
        return toResponse(vendor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VendorResponse> getAllVendors() {
        return vendorsRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private VendorResponse toResponse(Vendor vendor) {
        return new VendorResponse(
                vendor.getId(),
                vendor.getBusinessName() != null ? vendor.getBusinessName() : "",
                vendor.getGenres() != null ? vendor.getGenres() : new java.util.ArrayList<>(),
                vendor.getUserEmail(),
                vendor.getUser().getId());
    }
}