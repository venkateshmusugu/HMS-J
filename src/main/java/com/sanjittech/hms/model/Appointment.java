package com.sanjittech.hms.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long visitId;

    private LocalDate visitDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String departmentId;
    @Column(name = "reason_for_visit")
    private String reasonForVisit;



    @ManyToOne(fetch = FetchType.EAGER) // ← Change this
    @JoinColumn(name = "patient_id")
    @JsonIgnoreProperties(value = { "appointments" }) // Optional but good for avoiding loops
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id")
    @JsonIgnoreProperties(value = { "appointments" }) // If Doctor has appointments list too
    private Doctor doctor;
}