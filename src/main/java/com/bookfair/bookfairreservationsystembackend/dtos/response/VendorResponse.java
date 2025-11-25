package com.bookfair.bookfairreservationsystembackend.dtos.response;

import java.util.List;

public class VendorResponse {
    private Integer id;
    private String businessName;
    private List<String> genres;
    private String userEmail;
    private Integer userId;

    public VendorResponse() {
    }

    public VendorResponse(Integer id, String businessName, List<String> genres, String userEmail, Integer userId) {
        this.id = id;
        this.businessName = businessName;
        this.genres = genres;
        this.userEmail = userEmail;
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}