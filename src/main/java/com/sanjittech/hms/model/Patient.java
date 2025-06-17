package com.sanjittech.hms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@Builder
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long patientId;

    private String patientName;
    private String gender;
    private String phoneNumber;
    private int age;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;
    private String maritalStatus;
    private String caseDescription;

    // Relationships
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)

    private List<Appointment> appointments;

    @OneToMany(mappedBy = "patient")
    @JsonManagedReference
    private List<MedicalBill> bills;

    @OneToMany(mappedBy = "patient")
    @JsonManagedReference
    private List<Surgery> surgeries;

    @OneToMany(mappedBy = "patient")
    @JsonManagedReference
    private List<Treatment> treatments;

    @OneToMany(mappedBy = "patient")
    @JsonManagedReference
    private List<DoctorLog> doctorLogs;

    @OneToMany(mappedBy = "patient")
    @JsonManagedReference
    private List<MedicalHistory> medicalHistories;

    @OneToOne(mappedBy = "patient")
    @JsonManagedReference
    private PatientCase patientCase;



}



