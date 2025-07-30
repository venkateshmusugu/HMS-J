package com.sanjittech.hms.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "hospitals")
public class Hospital {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private String city;
    private String state;
    private String country;
    private String pincode;
    private String email;
    private String contactNumber;
    @Column(name = "icon_url")
    private String iconUrl;
    @Column(name = "plan")
    private String plan;

    @Column(name = "is_active")
    private boolean isActive = true;
    @Column(name = "payment_done")
    private boolean paymentDone;
    @Column(name = "registration_date")
    private LocalDate registrationDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "license_key")
    private String licenseKey;

}
