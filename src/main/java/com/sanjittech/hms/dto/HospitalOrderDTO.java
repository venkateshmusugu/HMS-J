package com.sanjittech.hms.dto;

import lombok.Data;

@Data
public class HospitalOrderDTO {
    private String name;
    private String address;
    private String city;
    private String state;
    private String country;
    private String pincode;
    private String iconUrl;
    private String email;
    private String plan;
    private String contactNumber;
    private String adminName;
    private String adminEmail;
    private String adminPassword;
}
