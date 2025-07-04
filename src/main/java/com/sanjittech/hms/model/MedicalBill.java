package com.sanjittech.hms.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalBill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long billId;

    private LocalDate billDate;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @Column(name = "created_time")
    private LocalTime createdTime;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonIgnoreProperties({"bills", "doctorLogs"})
    private Patient patient;

    @OneToMany(mappedBy = "medicalBill", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("medicalBill")
    private List<MedicalBillEntry> entries = new ArrayList<>();

    public Double getTotalAmount() {
        return entries.stream()
                .mapToDouble(MedicalBillEntry::getSubtotal)
                .sum();
    }

    @Override
    public String toString() {
        return "MedicalBill{" +
                "billId=" + billId +
                ", billDate=" + billDate +
                ", patientId=" + (patient != null ? patient.getPatientId() : null) +
                ", totalEntries=" + entries.size() +
                '}';
    }
}
