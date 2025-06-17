package com.sanjittech.hms.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    private String diagnosis;
    @OneToMany(mappedBy = "doctorLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Medicine> prescribedMedicines;

    private boolean followUpRequired;
    private LocalDate followUpDate;
    private String testType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    @JsonBackReference
    private Patient patient;

    @ManyToOne
    private Doctor doctor;

    @ManyToOne
    private Appointment appointment;
}