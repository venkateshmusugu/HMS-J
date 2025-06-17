package com.sanjittech.hms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medicine_id")  // ✅ Match the DB column name
    private Long medicineId;

    private String dosage;
    private String name;
    private int durationInDays;
    private String frequency;

    @ManyToOne
    @JoinColumn(name = "doctor_log_id")
    private DoctorLog doctorLog;

    // ✅ getters/setters
}

