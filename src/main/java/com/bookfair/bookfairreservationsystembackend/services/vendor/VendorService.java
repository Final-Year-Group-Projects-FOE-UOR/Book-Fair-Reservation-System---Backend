package com.bookfair.bookfairreservationsystembackend.services.vendor;

import com.bookfair.bookfairreservationsystembackend.dtos.request.VendorRequest;
import com.bookfair.bookfairreservationsystembackend.dtos.response.VendorResponse;

import java.util.List;

public interface VendorService {
    VendorResponse updateVendorByEmail(String userEmail, VendorRequest request);

    VendorResponse getVendorByUserEmail(String userEmail);

    List<VendorResponse> getAllVendors();
}
