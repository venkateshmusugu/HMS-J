package com.sanjittech.hms.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reasonForVisit;
    @Column
    private String diagnosis;
    @OneToMany(mappedBy = "doctorLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Medicine> medicines;
    @Column
    private boolean followUpRequired;
    @Column
    private LocalDate followUpDate;
    private String testType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    @JsonBackReference
    private Patient patient;


    @ManyToOne(fetch = FetchType.EAGER)
    private Doctor doctor;

    @ManyToOne
    @JsonIgnore
    private Appointment appointment;
}