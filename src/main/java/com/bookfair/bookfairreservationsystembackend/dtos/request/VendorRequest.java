package com.bookfair.bookfairreservationsystembackend.dtos.request;

import java.util.List;

public class VendorRequest {
    private String businessName;
    private List<String> genres;

    public VendorRequest() {
    }

    public VendorRequest(String businessName, List<String> genres) {
        this.businessName = businessName;
        this.genres = genres;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }
}