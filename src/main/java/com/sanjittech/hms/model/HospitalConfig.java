package com.sanjittech.hms.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class HospitalConfig {

    @Id
    private Long id = 1L;

    private String hospitalName;

    private String logoUrl; // URL to image (stored locally or S3, etc.)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
}