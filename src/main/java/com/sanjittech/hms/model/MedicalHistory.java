//package com.sanjittech.hms.model;
//
//import com.fasterxml.jackson.annotation.JsonBackReference;
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Entity
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class MedicalHistory {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long recordId;
//
//    private String additionalDescription;
//    private String prescription;
//    private String test;
//    private String medicalRecords;
//
//    @ManyToOne
//    @JoinColumn(name = "patient_id")
//    @JsonIgnoreProperties({"histories"})
//    private Patient patient;
//
//
//
//
//    @ManyToOne
//    private Doctor doctor;
//
//    @ManyToOne
//    private Appointment appointment;
//}
//
